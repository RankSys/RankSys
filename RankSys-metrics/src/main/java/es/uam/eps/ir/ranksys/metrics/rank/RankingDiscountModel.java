/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.rank;

/**
 * Ranking discount model. The furthest an item is from the top of a
 * recommendation list, the smaller the chance of the user seeing it.
 * This discount model determines the penalization of relevance according
 * to the rank of items in recommendation lists.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public interface RankingDiscountModel {

    /**
     * Discount to be applied at a given position.
     *
     * @param k position in the recommendation list starting from 0
     * @return discount to be applied for the given rank position
     */
    public double disc(int k);
}
