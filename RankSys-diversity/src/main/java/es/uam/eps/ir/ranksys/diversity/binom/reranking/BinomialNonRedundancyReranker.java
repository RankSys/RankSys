/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.binom.reranking;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.LambdaReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Binomial redundancy reranker.
 * 
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, redundancy
 * and size-awareness in genre diversity for Recommender Systems. RecSys 2014.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class BinomialNonRedundancyReranker<U, I, F> extends LambdaReranker<U, I> {

    private final FeatureData<I, F, ?> featureData;
    private final BinomialModel<U, I, F> binomialModel;

    /**
     * Constructor.
     *
     * @param featureData feature data
     * @param binomialModel binomial model
     * @param lambda trade-off between relevance and novelty
     * @param cutoff number of items to be greedily selected
     */
    public BinomialNonRedundancyReranker(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double lambda, int cutoff) {
        super(lambda, cutoff, true);
        this.featureData = featureData;
        this.binomialModel = binomialModel;
    }

    @Override
    protected BinomialNonRedundancyUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new BinomialNonRedundancyUserReranker(recommendation, maxLength);
    }

    /**
     * User re-ranker for {@link BinomialNonRedundancyReranker}.
     */
    protected class BinomialNonRedundancyUserReranker extends LambdaUserReranker {

        private final BinomialModel<U, I, F>.UserBinomialModel ubm;
        private final Object2IntOpenHashMap<F> featureCount;
        private final Object2DoubleMap<F> patienceNow;
        private final Object2DoubleMap<F> patienceLater;

        /**
         * Constructor.
         *
         * @param recommendation input recommendation to be re-ranked
         * @param maxLength number of items to be greedily selected
         */
        public BinomialNonRedundancyUserReranker(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            ubm = binomialModel.getModel(recommendation.getUser());

            featureCount = new Object2IntOpenHashMap<>();
            featureCount.defaultReturnValue(0);

            patienceNow = new Object2DoubleOpenHashMap<>();
            patienceLater = new Object2DoubleOpenHashMap<>();
            ubm.getFeatures().forEach(f -> {
                patienceNow.put(f, ubm.patience(0, f, cutoff));
                patienceLater.put(f, ubm.patience(1, f, cutoff));
            });
        }

        @Override
        protected double nov(Tuple2od<I> itemValue) {
            Set<F> itemFeatures = featureData.getItemFeatures(itemValue.v1)
                    .map(Tuple2::v1)
                    .collect(Collectors.toCollection(() -> new HashSet<>()));

            double iNonRed = featureCount.keySet().stream()
                    .mapToDouble(f -> {
                        if (itemFeatures.contains(f)) {
                            return patienceLater.getDouble(f);
                        } else {
                            return patienceNow.getDouble(f);
                        }
                    }).reduce((x, y) -> x * y).orElse(1.0);
            int m = featureCount.size() + (int) itemFeatures.stream()
                    .filter(f -> !featureCount.containsKey(f))
                    .count();
            iNonRed = Math.pow(iNonRed, 1 / (double) m);

            return iNonRed;
        }

        @Override
        protected void update(Tuple2od<I> bestItemValue) {
            featureData.getItemFeatures(bestItemValue.v1)
                    .map(Tuple2::v1)
                    .forEach(f -> {
                        int c = featureCount.addTo(f, 1) + 1;
                        patienceNow.put(f, patienceLater.getDouble(f));
                        patienceLater.put(f, ubm.patience(c + 1, f, cutoff));
                    });
        }

    }

}
