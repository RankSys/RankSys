/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.diversity.sales.metrics;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
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
        for (IdDouble<I> ivp : recommendation.getItems()) {
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
