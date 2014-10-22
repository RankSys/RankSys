/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
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
