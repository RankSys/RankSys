/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item.neighborhood;

import es.uam.eps.ir.ranksys.nn.item.sim.ItemSimilarity;
import es.uam.eps.ir.ranksys.nn.neighborhood.ThresholdNeighborhood;

/**
 * Threshold item neighborhood. See {@link ThresholdNeighborhood}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public class ThresholdItemNeighborhood<I> extends ItemNeighborhood<I>{

    /**
     * Constructor
     *
     * @param sim item similarity
     * @param threshold minimum value to be considered as neighbor
     */
    public ThresholdItemNeighborhood(ItemSimilarity<I> sim, double threshold) {
        super(sim, new ThresholdNeighborhood(sim, threshold));
    }
    
}
