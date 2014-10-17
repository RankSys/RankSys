/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics;

import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;

/**
 *
 * @author saul
 */
public interface SystemMetric<U, I> {

    public abstract void add(Recommendation<U, I> recommendation);

    public abstract double evaluate();
}
