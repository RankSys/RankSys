/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.distance;

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
