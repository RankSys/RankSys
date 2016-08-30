/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.sales.metrics;

import org.ranksys.metrics.rank.NoDiscountModel;
import org.ranksys.metrics.rel.NoRelevanceModel;

/**
 * Gini-Simpson index sales diversity metric. It is actually a relevance and rank-unaware version of {@link EIUDC} with a proportional mapping.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender Systems. PhD Thesis.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class GiniSimpsonIndex<U, I> extends EIUDC<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendation lists that is evaluated
     */
    public GiniSimpsonIndex(int cutoff) {
        super(cutoff, new NoDiscountModel(), new NoRelevanceModel<>());
    }

    /**
     * Evaluates the metric for the recommendations added so far.
     *
     * @return result of the metric for the recommendations previously added
     */
    @Override
    public double evaluate() {
        return (super.evaluate() - 1) / cutoff + 1;
    }

}
