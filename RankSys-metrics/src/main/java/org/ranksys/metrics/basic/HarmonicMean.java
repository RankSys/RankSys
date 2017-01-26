/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;

/**
 * Harmonic mean of two metrics.
 *
 * @param <U> user type
 * @param <I> item type
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
// TODO: more than two metrics
public class HarmonicMean<U, I> extends AbstractRecommendationMetric<U,I> {

    private final RecommendationMetric<U, I> metric1;
    private final RecommendationMetric<U, I> metric2;

    /**
     * Constructor
     *
     * @param metric1 metric 1
     * @param metric2 metric 2
     */
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
