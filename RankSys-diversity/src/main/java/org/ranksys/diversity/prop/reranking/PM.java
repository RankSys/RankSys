/* 
 * Copyright (C) 2015 RankSys (http://ranksys.org)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.diversity.prop.reranking;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.GreedyReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Proportionality re-ranking method.
 * <br>
 * 
 * Dang, V., Croft, W. B. (2012). Diversity by Proportionality: An Election-based Approach to Search Result Diversification. In Proceedings of the 35th International ACM SIGIR Conference on Research and Development in Information Retrieval (pp. 65–74). New York, NY, USA: ACM. doi:10.1145/2348283.2348296
 *
 * @author Saúl Vargas (saul.vargas@mendeley.com)
 *
 * @param <U> type of user
 * @param <I> type of item
 * @param <F> type of feature
 */
public class PM<U, I, F> extends GreedyReranker<U, I> {

    private final double lambda;
    private final FeatureData<I, F, ?> featureData;
    private final BinomialModel<U, I, F> binomialModel;

    /**
     * Constructor.
     *
     * @param binomialModel binomial model for features
     * @param featureData feature data
     * @param lambda relevance-diversity tradeoff
     * @param cutoff metric cutoff
     */
    public PM(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double lambda, int cutoff) {
        super(cutoff);
        this.lambda = lambda;
        this.featureData = featureData;
        this.binomialModel = binomialModel;
    }

    @Override
    protected GreedyUserReranker<U, I> getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new UserPM(recommendation, maxLength);
    }

    private class UserPM extends GreedyUserReranker<U, I> {

        private final BinomialModel<U, I, F>.UserBinomialModel ubm;
        private final Object2DoubleOpenHashMap<F> featureCount;
        private final Object2DoubleOpenHashMap<Object> probNorm;
        private F lcf;

        /**
         *
         * @param recommendation
         * @param maxLength
         */
        public UserPM(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            this.ubm = binomialModel.getModel(recommendation.getUser());
            this.featureCount = new Object2DoubleOpenHashMap<>();
            featureCount.defaultReturnValue(0.0);
            this.probNorm = new Object2DoubleOpenHashMap<>();
            recommendation.getItems().forEach(i -> {
                featureData.getItemFeatures(i.v1).sequential().forEach(fv -> {
                    probNorm.addTo(fv.v1, i.v2);
                });
            });
            this.lcf = getLcf();
        }

        private F getLcf() {
            return ubm.getFeatures().stream().max((F f1, F f2) -> Double.compare(quotient(f1), quotient(f2))).get();
        }

        private double quotient(F f) {
            return ubm.p(f) / (featureCount.getDouble(f) + 0.5);
        }

        @Override
        protected double value(Tuple2od<I> iv) {
            return featureData.getItemFeatures(iv.v1)
                    .map(Tuple2::v1)
                    .mapToDouble(f -> (f.equals(lcf) ? lambda : (1 - lambda)) * quotient(f) * iv.v2 / probNorm.getDouble(f))
                    .sum();
        }

        @Override
        protected void update(Tuple2od<I> biv) {
            double norm = featureData.getItemFeatures(biv.v1)
                    .map(Tuple2::v1)
                    .mapToDouble(f -> biv.v2 / probNorm.getDouble(f))
                    .sum();

            featureData.getItemFeatures(biv.v1).sequential()
                    .map(Tuple2::v1)
                    .forEach(f -> {
                        double v = biv.v2 / (probNorm.getDouble(f) * norm);
                        featureCount.addTo(f, v);
                    });

            lcf = getLcf();
        }

    }
}
