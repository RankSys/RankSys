/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item.neighborhood;

import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;

/**
 * Cached item neighborhood. See {@link CachedNeighborhood}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public class CachedItemNeighborhood<I> extends ItemNeighborhood<I> {

    /**
     * Constructor
     *
     * @param neighborhood item neighborhood to be cached
     */
    public CachedItemNeighborhood(ItemNeighborhood<I> neighborhood) {
        super(neighborhood, new CachedNeighborhood(neighborhood.numItems(), neighborhood));
    }
}
