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
package es.uam.eps.ir.ranksys.novelty.temporal;

import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
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
