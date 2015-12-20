/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.sales.metrics;

import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;

/**
 * Expected inter user discovery complement.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class EIUDC<U, I> extends AbstractSalesDiversityMetric<U, I> {

    /**
     * Constructor
     *
     * @param cutoff maximum length of the recommendation lists that is evaluated
     * @param disc ranking discount model
     * @param rel relevance model
     */
    public EIUDC(int cutoff, RankingDiscountModel disc, RelevanceModel<U, I> rel) {
        super(cutoff, disc, rel);
    }

    @Override
    protected double nov(I i) {
        return 1 - itemCount.getDouble(i) / numUsers;
    }

}
