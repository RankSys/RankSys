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

import es.uam.eps.ir.ranksys.metrics.rank.NoDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import static java.util.Arrays.sort;

/**
 * Gini index sales diversity metric.
 * 
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class GiniIndex<U, I> extends AbstractSalesDiversityMetric<U, I> {

    private final int numItems;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendation lists that is evaluated
     * @param numItems total number of items in the catalog
     */
    public GiniIndex(int cutoff, int numItems) {
        super(cutoff, new NoDiscountModel(), new NoRelevanceModel<>());
        this.numItems = numItems;
    }

    /**
     * Evaluates the metric for the recommendations added so far.
     *
     * @return result of the metric for the recommendations previously added
     */
    @Override
    public double evaluate() {
        double gi = 0;
        double[] cs = itemCount.values().toDoubleArray();
        sort(cs);
        for (int j = 0; j < cs.length; j++) {
            gi += (2 * (j + (numItems - cs.length) + 1) - numItems - 1) * (cs[j] / freeNorm);
        }
        gi /= (numItems - 1);
        gi = 1 - gi;

        return gi;
    }

    /**
     * Returns the sales novelty of an item.
     *
     * @param i item
     * @return the sales novelty of the item
     */
    @Override
    protected double nov(I i) {
        throw new UnsupportedOperationException("Using an alternative item novelty aggregation model");
    }
}
