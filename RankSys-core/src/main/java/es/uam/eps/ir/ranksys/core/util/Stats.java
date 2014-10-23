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
    private double oldM, newM, oldS, newS;

    public Stats() {
        n = 0;
    }
    
    public void accept(double x) {
        n++;
        
        if (n == 1) {
            oldM = x;
            newM = x;
            oldS = 0.0;
        } else {
            newM = oldM + (x - oldM) / n;
            newS = oldS + (x - oldM) * (x - newM);
            
            oldM = newM;
            oldS = newS;
        }
    }
    
    public void combine(Stats otherStats) {
        n += otherStats.n;
        throw new UnsupportedOperationException("to be implemented");
    }
    
    public int getN() {
        return n;
    }
    
    public double getMean() {
        return newM;
    }
    
    public double getVariance() {
        return (n > 1) ? (newS / (n - 1)) : 0.0;
    }
    
    public double getStandardDeviation() {
        return sqrt(getVariance());
    }
}
