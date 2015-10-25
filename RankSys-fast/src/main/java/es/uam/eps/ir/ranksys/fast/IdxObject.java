/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

/**
 * Pair of user/item/feature index with a typed object.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <V> type of the object
 */
public class IdxObject<V> implements Int2ObjectMap.Entry<V> {

    /**
     * User/item/feature index.
     */
    public int idx;

    /**
     * Typed object.
     */
    public V v;

    /**
     * Empty constructor.
     */
    public IdxObject() {
    }

    /**
     * Constructor.
     *
     * @param idx index
     * @param v object
     */
    public IdxObject(int idx, V v) {
        this.idx = idx;
        this.v = v;
    }
    
    /**
     * Re-fills the IdxObject object and returns itself.
     *
     * @param idx the index
     * @param v the object
     * @return this
     */
    public IdxObject refill(int idx, V v) {
        this.idx = idx;
        this.v = v;
        return this;
    }

    /**
     * Constructor that copies a index-object entry.
     *
     * @param e entry whose contents are copied
     */
    public IdxObject(Int2ObjectMap.Entry<V> e) {
        this.idx = e.getIntKey();
        this.v = e.getValue();
    }

    @Override
    public int getIntKey() {
        return idx;
    }

    @Override
    public Integer getKey() {
        return idx;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
