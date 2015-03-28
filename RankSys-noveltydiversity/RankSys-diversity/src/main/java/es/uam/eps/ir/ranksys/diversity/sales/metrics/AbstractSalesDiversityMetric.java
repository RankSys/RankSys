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
package es.uam.eps.ir.ranksys.diversity.sales.metrics;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.SystemMetric;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Abstract sales diversity metrics. It handles the counting of how many times
 * an item is recommended.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class AbstractSalesDiversityMetric<U, I> extends AbstractSystemMetric<U, I> {

    private final int cutoff;

    /**
     * Item count.
     */
    protected final Object2IntOpenHashMap<I> itemCount;

    /**
     * Total of recommended items, i.e., sum of the lengths of all
     * recommendations.
     */
    protected int m;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendation lists to evaluate.
     */
    public AbstractSalesDiversityMetric(int cutoff) {
        this.cutoff = cutoff;
        this.itemCount = new Object2IntOpenHashMap<>();
        itemCount.defaultReturnValue(0);
        this.m = 0;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        int rank = 0;
        for (IdDouble<I> ivp : recommendation.getItems()) {
            itemCount.addTo(ivp.id, 1);

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        m += rank;
    }

    @Override
    public void combine(SystemMetric<U, I> other) {
        ((AbstractSalesDiversityMetric<U, I>) other).itemCount.object2IntEntrySet().forEach(e -> {
            itemCount.addTo(e.getKey(), e.getIntValue());
        });

        m += ((AbstractSalesDiversityMetric<U, I>) other).m;
    }

    @Override
    public void reset() {
        this.m = 0;
        itemCount.clear();
    }

}
