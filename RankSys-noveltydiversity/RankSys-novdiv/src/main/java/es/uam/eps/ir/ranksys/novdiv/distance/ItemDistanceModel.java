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
package es.uam.eps.ir.ranksys.novdiv.distance;

import java.util.function.ToDoubleFunction;

/**
 * Item distance model.
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
 * @param <I> type of the items
 */
public interface ItemDistanceModel<I> {

    /**
     * Returns a function that return the distance to the input item.
     *
     * @param i item
     * @return function that return the distance to the input item
     */
    public ToDoubleFunction<I> dist(I i);
    
    /**
     * Returns the distance between a pair of items.
     *
     * @param i first item
     * @param j second item
     * @return distance between the items
     */
    public default double dist(I i, I j) {
        return dist(i).applyAsDouble(j);
    }
}
