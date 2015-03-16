/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware.metrics;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ERRIA<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ERRRelevanceModel<U, I> relModel;
    private final IntentModel<U, I, F> intentModel;

    public ERRIA(int cutoff, IntentModel<U, I, F> intentModel, ERRRelevanceModel<U, I> relevanceModel) {
        super();
        this.cutoff = cutoff;
        this.relModel = relevanceModel;
        this.intentModel = intentModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());
        IntentModel<U, I, F>.UserIntentModel uim = intentModel.getModel(recommendation.getUser());

        double[] erria = {0.0};

        Object2DoubleMap<F> pNoPrevRel = new Object2DoubleOpenHashMap<>();
        pNoPrevRel.defaultReturnValue(0.0);
        uim.getIntents().forEach((f) -> {
            pNoPrevRel.put(f, 1.0);
        });

        int[] rank = {0};
        for (IdDouble<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.id)) {
                double gain = userRelModel.gain(iv.id);
                uim.getItemIntents(iv.id).forEach(f -> {
                    double red = pNoPrevRel.getDouble(f);
                    erria[0] += uim.p(f) * gain * red / (1.0 + rank[0]);
                    pNoPrevRel.put(f, red * (1 - gain));
                });
            }
            rank[0]++;
            if (rank[0] >= cutoff) {
                break;
            }
        }

        return erria[0];
    }

    public static class ERRRelevanceModel<U, I> extends RelevanceModel<U, I> {

        private final RecommenderData<U, I, ?> testData;
        private final double threshold;
        private final double maxPreference;

        public ERRRelevanceModel(boolean caching, RecommenderData<U, I, ?> testData, double threshold) {
            super(caching, testData.getUsersWithPreferences());
            this.testData = testData;
            this.threshold = threshold;

            this.maxPreference = testData.getUsersWithPreferences().mapToDouble(u -> {
                return testData.getUserPreferences(u).mapToDouble(pref -> pref.v)
                        .max().orElse(Double.NEGATIVE_INFINITY);
            }).max().orElse(Double.NEGATIVE_INFINITY);
        }

        @Override
        protected UserRelevanceModel<U, I> get(U user) {
            return new UserERRRelevanceModel(user);
        }

        public class UserERRRelevanceModel implements UserRelevanceModel<U, I> {

            private final Object2DoubleMap<I> gainMap;

            public UserERRRelevanceModel(U user) {
                this.gainMap = new Object2DoubleOpenHashMap<>();
                gainMap.defaultReturnValue(0.0);

                testData.getUserPreferences(user)
                        .filter(iv -> iv.v >= threshold)
                        .forEach(iv -> gainMap.put(iv.id, (Math.pow(2, iv.v) - 1) / (double) Math.pow(2, maxPreference)));
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
