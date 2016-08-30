/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index;

import java.util.stream.Stream;

/**
 * Index for a set of items.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the items
 */
public interface ItemIndex<I> {

    /**
     * Checks whether the index contains an item.
     *
     * @param i item
     * @return true if the index contains the item, false otherwise
     */
    public boolean containsItem(I i);
    
    /**
     * Counts the number of indexed items.
     *
     * @return the total number of item
     */
    public int numItems();
    
    /**
     * Retrieves a stream of the indexed items.
     *
     * @return a stream of all the items
     */
    public Stream<I> getAllItems();
}
