/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.itemnovelty.metrics;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Item novelty metric.
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
public abstract class ItemNoveltyMetric<U, I> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;

    /**
     * item novelty model
     */
    protected final ItemNovelty<U, I> novelty;
    private final RelevanceModel<U, I> relModel;
    private final RankingDiscountModel disc;

    /**
     * Constructor.
     *
     * @param cutoff maximum size of the recommendation list that is evaluated
     * @param novelty novelty model
     * @param relevanceModel relevance model
     * @param disc ranking discount model
     */
    public ItemNoveltyMetric(int cutoff, ItemNovelty<U, I> novelty, RelevanceModel<U, I> relevanceModel, RankingDiscountModel disc) {
        super();
        this.cutoff = cutoff;
        this.novelty = novelty;
        this.relModel = relevanceModel;
        this.disc = disc;
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
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(u);
        ItemNovelty.UserItemNoveltyModel<U, I> uinm = novelty.getModel(u);
        
        if (uinm == null) {
            return 0.0;
        }

        double nov = 0.0;
        double norm = 0.0;

        int rank = 0;
        for (Tuple2od<I> iv : recommendation.getItems()) {
            nov += disc.disc(rank) * userRelModel.gain(iv.v1) * uinm.novelty(iv.v1);
            norm += disc.disc(rank);
            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        if (norm > 0.0) {
            nov /= norm;
        }

        return nov;
    }

}
