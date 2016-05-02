/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Simple implementation of FastItemIndex backed by a bi-map IdxIndex
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the items
 */
public class SimpleFastItemIndex<I> implements FastItemIndex<I>, Serializable {

    private final IdxIndex<I> iMap;

    /**
     * Constructor.
     *
     */
    protected SimpleFastItemIndex() {
        this.iMap = new IdxIndex<>();
    }

    @Override
    public boolean containsItem(I i) {
        return iMap.containsId(i);
    }

    @Override
    public int numItems() {
        return iMap.size();
    }

    @Override
    public Stream<I> getAllItems() {
        return iMap.getIds();
    }

    @Override
    public int item2iidx(I i) {
        return iMap.get(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iMap.get(iidx);
    }

    /**
     * Add a new item to the index. If the item already exists, nothing is done.
     *
     * @param i id of the item
     * @return index of the item
     */
    protected int add(I i) {
        return iMap.add(i);
    }

    /**
     * Creates an item index from a stream of item objects.
     *
     * @param <I> type of the items
     * @param items stream of item objects
     * @return a fast item index
     */
    public static <I> SimpleFastItemIndex<I> load(Stream<I> items) {
        SimpleFastItemIndex<I> itemIndex = new SimpleFastItemIndex<>();
        items.forEach(itemIndex::add);
        return itemIndex;
    }

}
