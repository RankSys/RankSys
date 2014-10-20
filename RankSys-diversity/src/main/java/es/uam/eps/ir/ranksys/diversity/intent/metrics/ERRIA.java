/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.intent.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.intent.IntentModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

/**
 *
 * @author saul
 */
public class ERRIA<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ERRRelevanceModel<U, I> relModel;
    private final IntentModel<U, I, F> intentModel;

    public ERRIA(int cutoff, RecommenderData<U, I, Double> trainData, IntentModel<U, I, F> intentModel, RecommenderData<U, I, Double> testData, double relThreshold) {
        this(cutoff, intentModel, new ERRRelevanceModel<>(testData, relThreshold));
    }

    public ERRIA(int cutoff, IntentModel<U, I, F> intentModel, ERRRelevanceModel<U, I> relevanceModel) {
        super();
        this.cutoff = cutoff;
        this.relModel = relevanceModel;
        this.intentModel = intentModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getUserModel(recommendation.getUser());
        IntentModel<U, I, F>.UserIntentModel uim = intentModel.getUserModel(recommendation.getUser());

        double[] erria = {0.0};

        TObjectDoubleMap<F> pNoPrevRel = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);
        uim.getIntents().forEach((f) -> {
            pNoPrevRel.put(f, 1.0);
        });

        int[] rank = {0};
        for (IdDoublePair<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.id)) {
                double gain = userRelModel.gain(iv.id);
                uim.getItemIntents(iv.id).forEach(f -> {
                    double red = pNoPrevRel.get(f);
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

    public static class ERRRelevanceModel<U, I> implements RelevanceModel<U, I> {

        private final RecommenderData<U, I, Double> testData;
        private final double threshold;
        private final double maxPreference;

        public ERRRelevanceModel(RecommenderData<U, I, Double> testData, double threshold) {
            this.testData = testData;
            this.threshold = threshold;

            this.maxPreference = testData.getAllUsers().mapToDouble(u -> {
                return testData.getUserPreferences(u).mapToDouble(pref -> pref.v)
                        .max().orElse(Double.NEGATIVE_INFINITY);
            }).max().orElse(Double.NEGATIVE_INFINITY);
        }

        @Override
        public RelevanceModel.UserRelevanceModel getUserModel(U user) {
            return new UserERRRelevanceModel(user);
        }

        public class UserERRRelevanceModel implements RelevanceModel.UserRelevanceModel<U, I> {

            private final TObjectDoubleMap<I> gainMap;

            public UserERRRelevanceModel(U user) {
                this.gainMap = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);

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
                return gainMap.get(item);
            }

            public double[] getGainValues() {
                return gainMap.values();
            }
        }
    }
}
