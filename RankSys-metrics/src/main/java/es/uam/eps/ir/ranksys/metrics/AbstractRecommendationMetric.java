/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics;

import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;

/**
 *
 * @author
 * saul
 */
public abstract class AbstractRecommendationMetric<U, I> implements RecommendationMetric<U, I> {

    public AbstractRecommendationMetric() {
    }

    @Override
    public abstract double evaluate(Recommendation<U, I> recommendation);
}
