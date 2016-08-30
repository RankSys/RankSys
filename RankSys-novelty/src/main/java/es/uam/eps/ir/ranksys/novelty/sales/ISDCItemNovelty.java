/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.sales;

import org.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty.UserItemNoveltyModel;
import java.util.List;
import java.util.function.Function;

/**
 * Inter-System Discovery Complement item novelty model.
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
public class ISDCItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final int nSystems;
    private final Function<U, List<List<I>>> otherUserRecommendations;
    private final RankingDiscountModel disc;

    /**
     * Constructor
     *
     * @param nSystems number of compared systems
     * @param otherUserRecommendations other systems recommendations
     * @param disc ranking discount model
     */
    public ISDCItemNovelty(int nSystems, Function<U, List<List<I>>> otherUserRecommendations, RankingDiscountModel disc) {
        super(false, null);
        this.nSystems = nSystems;
        this.otherUserRecommendations = otherUserRecommendations;
        this.disc = disc;
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U u) {
        return new UserISDCItemNoveltyModel(otherUserRecommendations.apply(u));
    }

    private class UserISDCItemNoveltyModel implements UserItemNoveltyModel<U, I> {

        private final List<List<I>> otherRecommendations;

        public UserISDCItemNoveltyModel(List<List<I>> otherRecommendations) {
            this.otherRecommendations = otherRecommendations;
        }

        @Override
        public double novelty(I i) {
            if (otherRecommendations == null) {
                return 1.0;
            }
            
            double nov = otherRecommendations.stream().mapToDouble(list -> {
                int k = list.indexOf(i);

                if (k == -1) {
                    return 1.0;
                } else {
                    return 1 - disc.disc(k);
                }
            }).sum();
            nov /= nSystems;

            return nov;
        }

    }

}
