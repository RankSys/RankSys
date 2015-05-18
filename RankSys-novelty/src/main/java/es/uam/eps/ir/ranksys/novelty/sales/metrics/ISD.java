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
package es.uam.eps.ir.ranksys.novelty.sales.metrics;

import es.uam.eps.ir.ranksys.metrics.rank.NoDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import es.uam.eps.ir.ranksys.novelty.sales.ISDCItemNovelty;
import java.util.List;
import java.util.function.Function;

/**
 * Inter-system diversity. It is actually a relevance and rank-unaware version of
 * {@link EISDC}.
 *
 * A. Bellogín, I. Cantador and P. Castells.  A study of heterogeneity in
 * recommendations for social music service. HetRec 2010.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ISD<U, I> extends EISDC<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum size of the recommendation list that is evaluated
     * @param nSystems number of compared systems
     * @param otherUserRecommendations other systems recommendations
     */
    public ISD(int cutoff, int nSystems, Function<U, List<List<I>>> otherUserRecommendations) {
        super(cutoff, new ISDCItemNovelty<>(nSystems, otherUserRecommendations, new NoDiscountModel()), new NoRelevanceModel<>(), new NoDiscountModel());
    }
    
}
