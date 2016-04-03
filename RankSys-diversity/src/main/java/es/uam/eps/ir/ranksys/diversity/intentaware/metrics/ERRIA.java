/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware.metrics;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel.UserIntentModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Intent-Aware Expected Reciprocal Rank metric.
 * 
 * R. Agrawal, S. Gollapudi, A. Halverson and S. Ieong, S. Diversifying search
 * results. WSDM 2009.
 * 
 * O. Chapelle, D. Metlzer, Y. Zhang, and P. Grinspan. Expected reciprocal 
 * rank for graded relevance. CIKM 2009.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class ERRIA<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ERRRelevanceModel<U, I> relModel;
    private final IntentModel<U, I, F> intentModel;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendations lists to evaluate
     * @param intentModel intent-aware model
     * @param relevanceModel relevance model
     */
    public ERRIA(int cutoff, IntentModel<U, I, F> intentModel, ERRRelevanceModel<U, I> relevanceModel) {
        super();
        this.cutoff = cutoff;
        this.relModel = relevanceModel;
        this.intentModel = intentModel;
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());
        UserIntentModel<U, I, F> uim = intentModel.getModel(recommendation.getUser());

        DoubleAdder erria = new DoubleAdder();

        Object2DoubleMap<F> pNoPrevRel = new Object2DoubleOpenHashMap<>();
        pNoPrevRel.defaultReturnValue(0.0);
        uim.getIntents().forEach(f -> pNoPrevRel.put(f, 1.0));

        AtomicInteger rank = new AtomicInteger();
        recommendation.getItems().stream().limit(cutoff).forEach(iv -> {
            if (userRelModel.isRelevant(iv.v1)) {
                double gain = userRelModel.gain(iv.v1);
                uim.getItemIntents(iv.v1).forEach(f -> {
                    double red = pNoPrevRel.getDouble(f);
                    erria.add(uim.pf_u(f) * gain * red / (1.0 + rank.intValue()));
                    pNoPrevRel.put(f, red * (1 - gain));
                });
            }
            rank.incrementAndGet();
        });

        return erria.doubleValue();
    }

    /**
     * Relevance model for {@link ERRIA}.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     */
    public static class ERRRelevanceModel<U, I> extends RelevanceModel<U, I> {

        private final PreferenceData<U, I> testData;
        private final double threshold;
        private final double maxPreference;

        /**
         * Constructor
         *
         * @param caching are the user relevance models being cached?
         * @param testData test preference data
         * @param threshold relevance threshold
         */
        public ERRRelevanceModel(boolean caching, PreferenceData<U, I> testData, double threshold) {
            super(caching, testData.getUsersWithPreferences());
            this.testData = testData;
            this.threshold = threshold;

            this.maxPreference = testData.getUsersWithPreferences().mapToDouble(u -> {
                return testData.getUserPreferences(u)
                        .mapToDouble(IdPref::v2)
                        .max().orElse(Double.NEGATIVE_INFINITY);
            }).max().orElse(Double.NEGATIVE_INFINITY);
        }

        @Override
        protected UserRelevanceModel<U, I> get(U user) {
            return new UserERRRelevanceModel(user);
        }

        /**
         * User relevance model for {@link ERRRelevanceModel}.
         */
        public class UserERRRelevanceModel implements UserRelevanceModel<U, I> {

            private final Object2DoubleMap<I> gainMap;

            /**
             * Constructor.
             *
             * @param user user whose relevance model is created
             */
            public UserERRRelevanceModel(U user) {
                this.gainMap = new Object2DoubleOpenHashMap<>();
                gainMap.defaultReturnValue(0.0);

                testData.getUserPreferences(user)
                        .filter(iv -> iv.v2 >= threshold)
                        .forEach(iv -> gainMap.put(iv.v1, (Math.pow(2, iv.v2) - 1) / (double) Math.pow(2, maxPreference)));
            }

            @Override
            public boolean isRelevant(I item) {
                return gainMap.containsKey(item);
            }

            @Override
            public double gain(I item) {
                return gainMap.getDouble(item);
            }
        }
    }
}
