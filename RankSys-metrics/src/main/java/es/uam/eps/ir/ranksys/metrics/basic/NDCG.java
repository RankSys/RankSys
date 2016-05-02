/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rank.LogarithmicDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.Arrays;
import java.util.Set;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Normalized Discounted Cumulative Gain metric.
 *
 * See: Kalervo Järvelin and Jaana Kekäläinen. 2000. IR evaluation methods for retrieving highly relevant documents. In Proceedings of the 23rd annual international ACM SIGIR conference on Research and development in information retrieval (SIGIR '00). ACM, New York, NY, USA, 41-48. DOI=10.1145/345508.345545 http://doi.acm.org/10.1145/345508.345545
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class NDCG<U, I> extends AbstractRecommendationMetric<U, I> {

    private final NDCGRelevanceModel<U, I> relModel;
    private final int cutoff;
    private final RankingDiscountModel disc;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of evaluated recommendation lists
     * @param relModel relevance model
     */
    public NDCG(int cutoff, NDCGRelevanceModel<U, I> relModel) {
        this.relModel = relModel;
        this.cutoff = cutoff;
        this.disc = new LogarithmicDiscountModel();
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        NDCGRelevanceModel<U, I>.UserNDCGRelevanceModel userRelModel = (NDCGRelevanceModel<U, I>.UserNDCGRelevanceModel) relModel.getModel(recommendation.getUser());

        double ndcg = 0.0;
        int rank = 0;

        for (Tuple2od<I> pair : recommendation.getItems()) {
            ndcg += userRelModel.gain(pair.v1) * disc.disc(rank);

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        if (ndcg > 0) {
            ndcg /= idcg(userRelModel);
        }

        return ndcg;
    }

    private double idcg(NDCGRelevanceModel.UserNDCGRelevanceModel relModel) {
        double[] gains = relModel.getGainValues();
        Arrays.sort(gains);

        double idcg = 0;
        int n = Math.min(cutoff, gains.length);
        int m = gains.length;

        for (int rank = 0; rank < n; rank++) {
            idcg += gains[m - rank - 1] * disc.disc(rank);
        }

        return idcg;
    }

    /**
     * Relevance model for nDCG, in which the gains of all relevant documents need to be known for the normalization of the metric.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     */
    public static class NDCGRelevanceModel<U, I> extends IdealRelevanceModel<U, I> {

        private final PreferenceData<U, I> testData;
        private final double threshold;

        /**
         * Constructors.
         *
         * @param caching are the user relevance models being cached?
         * @param testData test subset of preferences
         * @param threshold relevance threshold
         */
        public NDCGRelevanceModel(boolean caching, PreferenceData<U, I> testData, double threshold) {
            super(caching, testData.getUsersWithPreferences());
            this.testData = testData;
            this.threshold = threshold;
        }

        /**
         * {@inheritDoc}
         *
         * @param user input user
         * @return ndcg relevance model for input user
         */
        @Override
        protected UserNDCGRelevanceModel get(U user) {
            return new UserNDCGRelevanceModel(user);
        }

        /**
         * User relevance model for nDCG.
         *
         */
        public class UserNDCGRelevanceModel implements IdealRelevanceModel.UserIdealRelevanceModel<U, I> {

            private final Object2DoubleMap<I> gainMap;

            /**
             * Constructor.
             *
             * @param user user whose relevance model is computed
             */
            public UserNDCGRelevanceModel(U user) {
                this.gainMap = new Object2DoubleOpenHashMap<>();
                gainMap.defaultReturnValue(0.0);

                testData.getUserPreferences(user)
                        .filter(iv -> iv.v2 >= threshold)
                        .forEach(iv -> gainMap.put(iv.v1, Math.pow(2, iv.v2 - threshold + 1.0) - 1.0));
            }

            /**
             * {@inheritDoc}
             *
             * @return set of relevant items
             */
            @Override
            public Set<I> getRelevantItems() {
                return gainMap.keySet();
            }

            /**
             * {@inheritDoc}
             *
             * @param item input item
             * @return true is item is relevant, false otherwise
             */
            @Override
            public boolean isRelevant(I item) {
                return gainMap.containsKey(item);
            }

            /**
             * {@inheritDoc}
             *
             * @param item input item
             * @return relevance gain of the input item
             */
            @Override
            public double gain(I item) {
                return gainMap.getDouble(item);
            }

            /**
             * Get the vector of gains of the relevant items.
             *
             * @return array of positive relevance gains
             */
            public double[] getGainValues() {
                return gainMap.values().toDoubleArray();
            }
        }
    }

}
