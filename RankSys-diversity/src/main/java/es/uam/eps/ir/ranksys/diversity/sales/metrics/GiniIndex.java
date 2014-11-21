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

import static java.util.Arrays.sort;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class GiniIndex<U, I> extends AbstractSalesDiversityMetric<U, I> {

    private final int numItems;

    public GiniIndex(int cutoff, int numItems) {
        super(cutoff);
        this.numItems = numItems;
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
