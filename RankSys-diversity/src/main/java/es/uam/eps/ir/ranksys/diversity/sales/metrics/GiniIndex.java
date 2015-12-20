/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
