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
import org.ranksys.metrics.rel.RelevanceModel;

/**
 * Aggregate diversity. It is actually a rank-unaware version of {@link EIURD}
 * multiplied by the cut-off.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender 
 * Systems. PhD Thesis.
 *
 * G. Adomavicius and Y. Kwon. Improving aggregate recommendation diversity 
 * using rank-based techniques. TKDE vol. 24 no. 5, 2012.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class AggregateDiversityMetric<U, I> extends EIURD<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendation lists that is evaluated
     * @param relModel relevance model
     */
    public AggregateDiversityMetric(int cutoff, RelevanceModel<U, I> relModel) {
        super(cutoff, new NoDiscountModel(), relModel);
    }

    /**
     * Evaluates the metric for the recommendations added so far.
     *
     * @return result of the metric for the recommendations previously added
     */
    @Override
    public double evaluate() {
        return cutoff * super.evaluate();
    }

}
