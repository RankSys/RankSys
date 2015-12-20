/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Objects;

/**
 * A pair of a user/item/feature ID and a typed object.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the user/item/feature ID
 * @param <V> type of the object
 */
public class IdObject<I, V> implements Object2ObjectMap.Entry<I, V>, Comparable<IdObject<I, V>> {

    /**
     * The ID.
     */
    public I id;

    /**
     * The typed object.
     */
    public V v;

    /**
     * Empty constructor.
     */
    public IdObject() {
    }

    /**
     * Constructs an ID-object pair.
     *
     * @param id the ID
     * @param v the object
     */
    public IdObject(I id, V v) {
        this.id = id;
        this.v = v;
    }

    /**
     * Re-fills the IdObject object and returns itself.
     *
     * @param id the ID
     * @param v the object
     * @return this
     */
    public IdObject refill(I id, V v) {
        this.id = id;
        this.v = v;
        return this;
    }

    /**
     * Constructs an ID-object pair by copying an existing pair.
     *
     * @param e ID-object pair
     */
    public IdObject(Object2ObjectMap.Entry<I, V> e) {
        this(e.getKey(), e.getValue());
    }

    @Override
    public I getKey() {
        return id;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + Objects.hashCode(this.v);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IdObject<?, ?> other = (IdObject<?, ?>) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.v, other.v);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(IdObject<I, V> o) {
        int c = ((Comparable<I>) this.id).compareTo(o.id);
        if (c != 0) {
            return c;
        } else {
            return ((Comparable<V>) this.v).compareTo(o.v);
        }
    }

}
