/* 
 * Copyright (C) 2017 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.metrics.basic;

import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.ranksys.core.Recommendation;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import org.ranksys.core.preference.IdPref;
import org.ranksys.core.preference.PreferenceData;
import org.ranksys.metrics.AbstractRecommendationMetric;
import org.ranksys.metrics.rel.RelevanceModel;

/**
 * Expected Reciprocal Rank metric
 * O. Chapelle, D. Metlzer, Y. Zhang, and P. Grinspan. Expected reciprocal 
 * rank for graded relevance. CIKM 2009.
 * @author Javier Sanz-Cruzado Puig
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ERR<U,I> extends AbstractRecommendationMetric<U,I>
{

    private final int cutoff;
    private final ERRRelevanceModel<U, I> relModel;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendations lists to evaluate
     * @param relevanceModel relevance model
     */
    public ERR(int cutoff, ERRRelevanceModel<U, I> relevanceModel) {
        super();
        this.cutoff = cutoff;
        this.relModel = relevanceModel;
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) 
    {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());

        DoubleAdder erria = new DoubleAdder();

        AtomicInteger rank = new AtomicInteger();
        AtomicDouble prevNoRel = new AtomicDouble();
        prevNoRel.set(1.0);
        recommendation.getItems().stream().limit(cutoff).forEach(iv -> 
        {
            if (userRelModel.isRelevant(iv.v1)) 
            {
                
                double gain = userRelModel.gain(iv.v1);
                double prevNoRelValue = prevNoRel.doubleValue();
                erria.add(gain/(1.0 + rank.intValue())*prevNoRel.doubleValue());
                prevNoRel.getAndSet(prevNoRel.doubleValue()*(1.0-gain));
                
            }            
            rank.incrementAndGet();
        });

        return erria.doubleValue();
    }
    
    /**
     * Relevance model for {@link ERR}.
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
        protected RelevanceModel.UserRelevanceModel<U, I> get(U user) {
            return new UserERRRelevanceModel(user);
        }

        /**
         * User relevance model for {@link ERRRelevanceModel}.
         */
        public class UserERRRelevanceModel implements RelevanceModel.UserRelevanceModel<U, I> {

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
