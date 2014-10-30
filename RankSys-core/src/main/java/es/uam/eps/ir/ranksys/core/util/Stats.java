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
package es.uam.eps.ir.ranksys.core.util;

import static java.lang.Math.sqrt;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Stats {

    private int n;
    private double m, s;
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;

    public Stats() {
        n = 0;
    }

    public void accept(double x) {
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

    public void combine(Stats otherStats) {
        n += otherStats.n;
        
        
        
        max = Math.max(max, otherStats.max);
        min = Math.min(min, otherStats.min);
        throw new UnsupportedOperationException("to be implemented");
    }

    public int getN() {
        return n;
    }

    public double getMean() {
        return m;
    }

    public double getVariance() {
        return (n > 1) ? (s / (n - 1)) : 0.0;
    }

    public double getStandardDeviation() {
        return sqrt(getVariance());
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

}
