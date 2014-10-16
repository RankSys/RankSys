package es.uam.eps.ir.ranksys.core.util;

import static java.lang.Math.sqrt;

public class Stats {
    
    private int n;
    double oldM, newM, oldS, newS;

    public Stats() {
        n = 0;
    }
    
    public void clear() {
        n = 0;
    }
    
    public void increment(double x) {
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
