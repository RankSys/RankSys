/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.metrics;

import es.uam.eps.ir.ranksys.core.Recommendation;

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
     * Evaluates the metrics added so far.
     *
     * @return result of the metrics for the recommendations previously added
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
