package org.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;

// TODO: more than two metrics
public class HarmonicMean<U, I> extends AbstractRecommendationMetric<U,I> {

    private final RecommendationMetric<U, I> metric1;
    private final RecommendationMetric<U, I> metric2;

    public HarmonicMean(RecommendationMetric<U, I> metric1, RecommendationMetric<U, I> metric2) {
        this.metric1 = metric1;
        this.metric2 = metric2;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        double p = metric1.evaluate(recommendation);
        double r = metric2.evaluate(recommendation);

        return p == 0.0 || r == 0.0 ? 0.0 : 2 * p * r / (p + r);
    }
}
