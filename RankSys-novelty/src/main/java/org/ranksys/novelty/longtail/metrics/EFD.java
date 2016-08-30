/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novelty.longtail.metrics;

import org.ranksys.novdiv.itemnovelty.metrics.ItemNoveltyMetric;
import org.ranksys.novelty.longtail.FDItemNovelty;
import org.ranksys.metrics.rank.RankingDiscountModel;
import org.ranksys.metrics.rel.RelevanceModel;

/**
 * Expected free discovery metric.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 * 
 * S. Vargas and P. Castells. Rank and relevance in novelty and diversity for
 * Recommender Systems. RecSys 2011.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class EFD<U, I> extends ItemNoveltyMetric<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum size of the recommendation list that is evaluated
     * @param novelty novelty model
     * @param relModel relevance model
     * @param disc ranking discount model
     */
    public EFD(int cutoff, FDItemNovelty<U, I> novelty, RelevanceModel<U, I> relModel, RankingDiscountModel disc) {
        super(cutoff, novelty, relModel, disc);
    }

}
