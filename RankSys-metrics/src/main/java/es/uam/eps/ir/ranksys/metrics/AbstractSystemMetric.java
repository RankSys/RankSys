/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;

/**
 *
 * @author saul
 */
public abstract class AbstractSystemMetric<U, I, V> implements SystemMetric<U, I> {

    protected final RecommenderData<U, I, V> testData;

    public AbstractSystemMetric(RecommenderData<U, I, V> testData) {
        this.testData = testData;
    }

    public static abstract class AbstractFactory<U, I, V> implements SystemMetric.Factory<U, I> {

        protected final RecommenderData<U, I, V> testData;

        public AbstractFactory(RecommenderData<U, I, V> testData) {
            this.testData = testData;
        }

    }
}
