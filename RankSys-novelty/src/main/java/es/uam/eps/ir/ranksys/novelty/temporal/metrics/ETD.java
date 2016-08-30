/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.temporal.metrics;

import org.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.novdiv.itemnovelty.metrics.ItemNoveltyMetric;
import org.ranksys.metrics.rank.RankingDiscountModel;
import org.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.novelty.temporal.TDItemNovelty;

/**
 * Expected Temporal Discovery metric.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 * 
 * N. Lathia, S. Hailes, L. Capra, X. Amatriain. Temporal diversity in
 * Recommender Systems. SIGIR 2010.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ETD<U, I> extends ItemNoveltyMetric<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum size of the recommendation list that is evaluated
     * @param novelty novelty model
     * @param relevanceModel relevance model
     * @param disc ranking discount model
     */
    public ETD(int cutoff, TDItemNovelty<U, I> novelty, RelevanceModel<U, I> relevanceModel, RankingDiscountModel disc) {
        super(cutoff, novelty, relevanceModel, disc);
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        U u = recommendation.getUser();
        ItemNovelty.UserItemNoveltyModel uinm = novelty.getModel(u);
        
        if (uinm == null) {
            return Double.NaN;
        } else {
            return super.evaluate(recommendation);
        }
    }

}
