/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.sales.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 *
 * @author saul
 */
public abstract class AbstractSalesDiversityMetric<U, I> extends AbstractSystemMetric<U, I> {

    private final int cutoff;
    protected final TObjectIntMap<I> itemCount;
    protected int m;

    public AbstractSalesDiversityMetric(int cutoff) {
        this.cutoff = cutoff;
        this.itemCount = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);
        this.m = 0;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        int rank = 0;
        for (IdDoublePair<I> ivp : recommendation.getItems()) {
            itemCount.adjustOrPutValue(ivp.id, 1, 1);

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        m += rank;
    }

    @Override
    public void combine(SystemMetric<U, I> other) {
        ((AbstractSalesDiversityMetric<U, I>) other).itemCount.forEachEntry((k, v) -> {
            itemCount.adjustOrPutValue(k, v, v);
            return true;
        });

        m += ((AbstractSalesDiversityMetric<U, I>) other).m;
    }

    @Override
    public void reset() {
        this.m = 0;
        itemCount.clear();
    }

}
