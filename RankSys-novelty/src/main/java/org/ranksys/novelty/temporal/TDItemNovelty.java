/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novelty.temporal;

import org.ranksys.novdiv.itemnovelty.ItemNovelty;
import org.ranksys.metrics.rank.RankingDiscountModel;
import java.util.List;
import java.util.function.Function;

/**
 * Temporal discovery item novelty model.
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
public class TDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final Function<U, List<I>> pastRecommendations;
    private final RankingDiscountModel disc;

    /**
     * Constructor.
     *
     * @param pastRecommendations previous recommendations
     * @param disc ranking discount model
     */
    public TDItemNovelty(Function<U, List<I>> pastRecommendations, RankingDiscountModel disc) {
        super();
        this.pastRecommendations = pastRecommendations;
        this.disc = disc;
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U u) {
        List<I> lastRecommendation = pastRecommendations.apply(u);
        if (lastRecommendation == null) {
            return null;
        } else {
            return new UserTimeItemNoveltyModel(lastRecommendation);
        }
    }

    private class UserTimeItemNoveltyModel implements UserItemNoveltyModel<U, I> {

        private final List<I> lastRecommendation;

        public UserTimeItemNoveltyModel(List<I> lastRecommendation) {
            this.lastRecommendation = lastRecommendation;
        }

        @Override
        public double novelty(I i) {
            int k = lastRecommendation.indexOf(i);

            if (k == -1) {
                return 1.0;
            } else {
                return 1 - disc.disc(k);
            }
        }

    }

}
