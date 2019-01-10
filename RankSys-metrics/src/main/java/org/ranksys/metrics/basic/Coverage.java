/*
 *  Copyright (C) 2016 Information Retrieval Group at Universidad Aut�noma
 *  de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.metrics.basic;

import java.util.ArrayList;
import java.util.List;
import org.ranksys.core.Recommendation;
import org.ranksys.metrics.SystemMetric;

/**
 * Finds the coverage of a recommender: how many of the possible recommendations
 * are provided.
 * @author Javier Sanz-Cruzado Puig
 * @param <U> type of the users.
 * @param <I> type of the items.
 */
public class Coverage<U,I> implements SystemMetric<U,I> 
{
    /**
     * Recommendation cutoff
     */
    private final int cutoff;
    
    /**
     * Length of the recommendation
     */
    private final List<Double> recs;
    
    /**
     * Constructor.
     * @param cutoff recommendation cutoff.
     */
    public Coverage(int cutoff)
    {
        this.cutoff = cutoff;
        this.recs = new ArrayList<>();
    }
    
    @Override
    public void add(Recommendation<U, I> r) 
    {
        recs.add(Math.min(cutoff,r.getItems().size()+ 0.0));
    }

    @Override
    public double evaluate() 
    {
        double total = this.cutoff*recs.size();
        double real = recs.stream().mapToDouble(x -> x).sum();
        return real/total;
    }

    @Override
    public void combine(SystemMetric<U, I> sm) 
    {
        if(this.getClass().isInstance(sm))
        {
            Coverage<U,I> other = (Coverage<U,I>) sm;
            other.recs.forEach(x -> this.recs.add(Math.min(x, cutoff)));
        }
    }

    @Override
    public void reset() 
    {
        recs.clear();
    }
    
}
