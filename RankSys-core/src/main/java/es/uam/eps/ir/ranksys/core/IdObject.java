/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.core;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

/**
 * A pair of a user/item/feature ID and a typed object.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the user/item/feature ID
 * @param <V> type of the object
 */
public class IdObject<I, V> implements Object2ObjectMap.Entry<I, V> {

    /**
     * The ID.
     */
    public I id;

    /**
     * The typed object.
     */
    public V v;

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

}
