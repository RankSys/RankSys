/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.novelty.sales;

import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
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
