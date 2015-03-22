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
    public final int idx;

    /**
     * Typed object.
     */
    public final V v;

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
