/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index.updateable;

import java.util.stream.Stream;
import org.ranksys.core.index.ItemIndex;

/**
 * Updateable index for a set of items.
 * 
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the items
 */
public interface UpdateableItemIndex<I> extends ItemIndex<I>
{
    /**
     * Adds a new item.
     * @param i the item.
     * @return the identifier of the new item
     */
    public int addItem(I i);
        
    /**
     * Removes a item from the index.
     * @param i the item to delete. 
     * @return the old identifier of the item.
     */
    //public int removeItem(I i);
    
    /**
     * Adds a set of items to the index
     * @param items a stream containing the items to add.
     */
    public default void addItems(Stream<I> items)
    {
        items.forEach(i -> this.addItem(i));
    }
}
