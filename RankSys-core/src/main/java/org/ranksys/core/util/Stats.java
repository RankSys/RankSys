/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util;

import static java.lang.Math.sqrt;

/**
 * Calculate common statistics for samples of doubles
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Stats {
    
    private long n;
    private double m, s;
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;

    /**
     * Constructor.
     */
    public Stats() {
        n = 0;
    }

    /**
     * Adds a value and updates the statistics.
     * 
     * If the input value is a NaN, it is ignored.
     * 
     * @param x value to be added
     */
    public void accept(double x) {
        if (Double.isNaN(x)) {
            return;
        }
        
        n++;

        if (n == 1) {
            m = x;
            s = 0.0;
        } else {
            double oldM = m;
            double oldS = s;
            
            m = oldM + (x - oldM) / n;
            s = oldS + (x - oldM) * (x - m);
        }
        
        max = Math.max(max, x);
        min = Math.min(min, x);
    }

    /**
     * Combines the statistics for another sample into this.
     * 
     * This is useful for performing mutable reductions (Stream.collect).
     * Unfortunately, the current implementation cannot combine the variances of the samples.
     *
     * @param o statistics of other sample
     */
    public void combine(Stats o) {
        max = Math.max(max, o.max);
        min = Math.min(min, o.min);
        m = m * (n / (double) (n + o.n)) + o.m * (o.n / (double) (n + o.n));
        s = Double.NaN;
        n += o.n;
    }

    /**
     * Returns the size of the sample.
     *
     * @return size of the sample
     */
    public long getN() {
        return n;
    }

    /**
     * Returns the mean of the sample.
     *
     * @return mean
     */
    public double getMean() {
        return m;
    }

    /**
     * Returns the variance of the sample.
     *
     * @return variance
     */
    public double getVariance() {
        return (n > 1) ? (s / (n - 1)) : Double.NaN;
    }

    /**
     * Returns the standard deviation of the sample.
     *
     * @return standard deviation
     */
    public double getStandardDeviation() {
        return sqrt(getVariance());
    }

    /**
     * Returns the greatest value of the sample.
     *
     * @return maximum value
     */
    public double getMax() {
        return max;
    }

    /**
     * Returns the smallest value of the sample.
     * 
     * @return minimum value
     */
    public double getMin() {
        return min;
    }
    
    /**
     * Resets the Stats object (as it had just been created).
     */
    public void reset() {
        n = 0;
        m = 0.0;
        s = 0.0;
        max = Double.NEGATIVE_INFINITY;
        min = Double.POSITIVE_INFINITY;
    }

}
