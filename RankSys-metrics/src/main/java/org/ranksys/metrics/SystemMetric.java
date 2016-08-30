/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.metrics;

import org.ranksys.core.Recommendation;

/**
 * System metric: a metric that evaluates recommendations to a community of
 * users as a whole. Can be computed in parallel fashion via mutable reduction,
 * i.e., Stream.collect()
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface SystemMetric<U, I> {

    /**
     * Add a recommendation to the metric.
     *
     * @param recommendation recommendation to be added to the computation.
     */
    public abstract void add(Recommendation<U, I> recommendation);

    /**
     * Evaluates the metric for the recommendations added so far.
     *
     * @return result of the metric for the recommendations previously added
     */
    public abstract double evaluate();
    
    /**
     * Combines the recommendations added to other system metric to this one.
     *
     * @param other other system metric, should be of the same or compatible
     * class
     */
    public abstract void combine(SystemMetric<U, I> other);
    
    /**
     * Resets the metric by discarding the recommendations previously added.
     *
     */
    public abstract void reset();
}
