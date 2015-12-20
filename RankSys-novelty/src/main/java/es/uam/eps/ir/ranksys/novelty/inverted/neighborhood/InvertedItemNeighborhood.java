/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.inverted.neighborhood;

import es.uam.eps.ir.ranksys.nn.item.neighborhood.ItemNeighborhood;

/**
 * Inverted item neighborhood. See {@link InvertedNeighborhood}
 * 
 * S. Vargas and P. Castells. Improving sales diversity by recommending
 * users to items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public class InvertedItemNeighborhood<I> extends ItemNeighborhood<I> {

    /**
     * Constructor.
     *
     * @param neighborhood original neighborhood to be inverted
     */
    public InvertedItemNeighborhood(ItemNeighborhood<I> neighborhood) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numItems(), neighborhood, iidx -> true));
    }
    
}
