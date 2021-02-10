/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.binom.reranking;

import org.ranksys.core.feature.item.ItemFeatureData;
import org.ranksys.core.Recommendation;
import org.ranksys.novdiv.binom.BinomialModel;
import org.ranksys.novdiv.normalizer.Normalizer;
import org.ranksys.novdiv.normalizer.Normalizers;
import org.ranksys.novdiv.normalizer.ZScoreNormalizer;
import org.ranksys.novdiv.reranking.LambdaReranker;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Binomial coverage reranker.
 * 
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, redundancy
 * and size-awareness in genre diversity for Recommender Systems. RecSys 2014.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class BinomialCoverageReranker<U, I, F> extends LambdaReranker<U, I> {

    private final ItemFeatureData<I, F, ?> featureData;
    private final BinomialModel<U, I, F> binomialModel;

    /**
     * Constructor.
     *
     * @param featureData feature data
     * @param binomialModel binomial model
     * @param lambda trade-off between relevance and novelty
     * @param cutoff number of items to be greedily selected
     */
    public BinomialCoverageReranker(ItemFeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double lambda, int cutoff) {
        super(lambda, cutoff, Normalizers.zscore());
        this.featureData = featureData;
        this.binomialModel = binomialModel;
    }

    /**
     * Constructor.
     *
     * @param featureData feature data
     * @param binomialModel binomial model
     * @param lambda trade-off between relevance and novelty
     * @param cutoff number of items to be greedily selected
     * @param norm a supplier for normalizing functions
     */
    public BinomialCoverageReranker(ItemFeatureData<I,F,?> featureData, BinomialModel<U,I,F> binomialModel, double lambda, int cutoff, Supplier<Normalizer<I>> norm)
    {
        super(lambda, cutoff, norm);
        this.featureData = featureData;
        this.binomialModel = binomialModel;
    }

    @Override
    protected BinomialCoverageUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new BinomialCoverageUserReranker(recommendation, maxLength);
    }

    /**
     * User re-ranker for {@link BinomialCoverageReranker}.
     */
    public class BinomialCoverageUserReranker extends LambdaUserReranker {

        private final BinomialModel<U, I, F>.UserBinomialModel ubm;
        private final Set<F> uncoveredFeatures;
        private double coverage;

        /**
         * Constructor.
         *
         * @param recommendation input recommendation to be re-ranked
         * @param maxLength number of items to be greedily selected
         */
        public BinomialCoverageUserReranker(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            ubm = binomialModel.getModel(recommendation.getUser());

            uncoveredFeatures = new HashSet<>(ubm.getFeatures());
            coverage = uncoveredFeatures.stream()
                    .mapToDouble(f -> ubm.longing(f, cutoff))
                    .reduce((x, y) -> x * y).orElse(1.0);
            coverage = Math.pow(coverage, 1 / (double) ubm.getFeatures().size());

        }

        @Override
        protected double nov(Tuple2od<I> itemValue) {
            double iCoverage = featureData.getItemFeatures(itemValue.v1)
                    .map(Tuple2::v1)
                    .filter(uncoveredFeatures::contains)
                    .mapToDouble(f -> ubm.longing(f, cutoff))
                    .reduce((x, y) -> x * y).orElse(1.0);
            iCoverage = Math.pow(iCoverage, 1 / (double) ubm.getFeatures().size());
            iCoverage = coverage / iCoverage;

            return iCoverage;
        }

        @Override
        protected void update(Tuple2od<I> bestItemValue) {
            double iCoverage = featureData.getItemFeatures(bestItemValue.v1).sequential()
                    .map(Tuple2::v1)
                    .filter(uncoveredFeatures::remove)
                    .mapToDouble(f -> ubm.longing(f, cutoff))
                    .reduce((x, y) -> x * y).orElse(1.0);
            iCoverage = Math.pow(iCoverage, 1 / (double) ubm.getFeatures().size());
            coverage /= iCoverage;
        }

    }

}
