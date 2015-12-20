/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index;

import es.uam.eps.ir.ranksys.core.index.ItemIndex;

/**
 * Mutable item index.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * @param <I> item type
 */
public interface MutableItemIndex<I> extends ItemIndex<I> {

    /**
     * Adds item with an automatically generated id.
     *
     * @return the id of the item created
     */
    public I addItem();

    /**
     * Add item with specified id.
     *
     * @param i id of the item
     * @return true if new item was added, false otherwise
     */
    public boolean addItem(I i);

    /**
     * Removes item
     *
     * @param i id of the item
     * @return true if item was removed, false otherwise
     */
    public boolean removeItem(I i);
}
