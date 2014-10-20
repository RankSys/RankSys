/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics.rel;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author saul
 */
public class BinaryRelevanceModel<U, I> implements IdealRelevanceModel<U, I> {

    private final RecommenderData<U, I, Double> testData;
    private final double threshold;

    public BinaryRelevanceModel(RecommenderData<U, I, Double> testData, double threshold) {
        this.testData = testData;
        this.threshold = threshold;
    }

    @Override
    public UserIdealRelevanceModel getUserModel(U user) {
        return new UserBinaryRelevanceModel(user);
    }

    private class UserBinaryRelevanceModel implements UserIdealRelevanceModel<U, I> {

        private final Set<I> relevantItems;

        public UserBinaryRelevanceModel(U user) {
            this.relevantItems = testData.getUserPreferences(user)
                    .filter(iv -> iv.v >= threshold)
                    .map(iv -> iv.id)
                    .collect(Collectors.toSet());
        }

        @Override
        public Set<I> getRelevantItems() {
            return relevantItems;
        }

        @Override
        public boolean isRelevant(I item) {
            return relevantItems.contains(item);
        }

        @Override
        public double gain(I item) {
            return isRelevant(item) ? 1.0 : 0.0;
        }

    }
}
