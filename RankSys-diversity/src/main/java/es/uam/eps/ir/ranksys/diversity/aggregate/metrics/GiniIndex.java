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
package es.uam.eps.ir.ranksys.diversity.aggregate.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import static java.util.Arrays.sort;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class GiniIndex<U, I> extends AbstractSystemMetric<U, I> {

    private final int cutoff;
    private final TObjectIntMap<I> itemCount;
    private final int numItems;
    private int m;

    public GiniIndex(int cutoff, int numItems) {
        this.cutoff = cutoff;
        this.itemCount = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);

        this.numItems = numItems;

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
    public double evaluate() {
        double gi = 0;
        int[] cs = itemCount.values();
        itemCount.clear();
        sort(cs);
        for (int j = 0; j < cs.length; j++) {
            gi += (2 * (j + (numItems - cs.length) + 1) - numItems - 1) * (cs[j] / (double) m);
        }
        gi /= (numItems - 1);
        gi = 1 - gi;

        return gi;
    }
}
