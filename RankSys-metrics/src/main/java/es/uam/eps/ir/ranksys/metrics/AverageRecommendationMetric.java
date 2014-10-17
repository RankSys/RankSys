/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics;

import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;

/**
 *
 * @author saul
 */
public class AverageRecommendationMetric<U, I> extends AbstractSystemMetric<U, I> {

    private final RecommendationMetric<U, I> metric;
    private double sum;
    private final int numUsers;

    public AverageRecommendationMetric(RecommendationMetric<U, I> metric, int numUsers) {
        this.metric = metric;
        this.sum = 0;
        this.numUsers = numUsers;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        sum += metric.evaluate(recommendation);
    }

    @Override
    public double evaluate() {
        return sum / numUsers;
    }
}
