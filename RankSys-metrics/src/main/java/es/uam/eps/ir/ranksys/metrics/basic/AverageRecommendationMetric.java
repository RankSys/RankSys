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
package es.uam.eps.ir.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;

/**
 * Average of a recommendation metric: system metric based on the arithmetic
 * mean of a recommendation metric for a set of users' recommendations.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class AverageRecommendationMetric<U, I> extends AbstractSystemMetric<U, I> {

    private final RecommendationMetric<U, I> metric;
    private double sum;
    private int numUsers;
    private final boolean allUsers;
    private final boolean ignoreNaN;

    /**
     * Constructor in which the number of users of the recommendation metric to
     * be averaged is specified. Recommendations returning NaN or missing 
     * recommendations are treated as zeros to the average.
     *
     * @param metric recommendation metric to be averaged
     * @param numUsers number of expected users' recommendations
     */
    public AverageRecommendationMetric(RecommendationMetric<U, I> metric, int numUsers) {
        this.metric = metric;
        this.sum = 0;
        this.numUsers = numUsers;
        this.allUsers = true;
        this.ignoreNaN = false;
    }

    /**
     * Constructor in which the average is calculated for all the recommendations
     * added during the calculation.
     *
     * @param metric recommendation metric to be averaged
     * @param ignoreNaN ignore NaNs from the calculation of the average?
     */
    public AverageRecommendationMetric(RecommendationMetric<U, I> metric, boolean ignoreNaN) {
        this.metric = metric;
        this.sum = 0;
        this.numUsers = 0;
        this.allUsers = false;
        this.ignoreNaN = ignoreNaN;
    }

    /**
     * Adds the recommendation metric to the average and returns the user value.
     *
     * @param recommendation recommendation to be added
     * @return results of the recommender metric
     */
    public double addAndEvaluate(Recommendation<U, I> recommendation) {
        double v = metric.evaluate(recommendation);

        if (!ignoreNaN || !Double.isNaN(v)) {
            sum += v;

            if (!allUsers) {
                numUsers++;
            }
        }

        return v;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        addAndEvaluate(recommendation);
    }

    @Override
    public void combine(SystemMetric<U, I> other) {
        sum += ((AverageRecommendationMetric<U, I>) other).sum;

        if (!allUsers) {
            numUsers += ((AverageRecommendationMetric<U, I>) other).numUsers;
        }
    }

    @Override
    public double evaluate() {
        return sum / numUsers;
    }

    @Override
    public void reset() {
        this.sum = 0;
        this.numUsers = 0;
    }

}
