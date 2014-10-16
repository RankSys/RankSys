/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics.rel;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author saul
 */
public class NoRelevanceModel<U, I> implements RelevanceModel<U, I> {

    private final Set<I> items;

    public NoRelevanceModel(RecommenderData<U, I, ?> testData) {
        this.items = new HashSet<>();
        testData.getAllItems().forEach(item -> items.add(item));
    }

    @Override
    public UserRelevanceModel getUserModel(U user) {
        return new UserNoRelevanceModel();
    }

    private class UserNoRelevanceModel implements UserRelevanceModel<U, I> {

        @Override
        public Set<I> getRelevantItems() {
            return items;
        }

        @Override
        public boolean isRelevant(I item) {
            return true;
        }

        @Override
        public double gain(I item) {
            return 1.0;
        }
        
    }
    
}
