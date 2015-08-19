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
package es.uam.eps.ir.ranksys.fast.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Bi-map-like structure to back fast version of user/item/feature indexes. It keeps to maps: id-to-index and index-to-id. Value of indexes go from 0 (included) to the number of elements (excluded).
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <T> type of the user/item/feature
 */
public class IdxIndex<T> implements Serializable {

    private final Object2IntMap<T> t2imap;
    private final List<T> i2tmap;

    /**
     * Constructor.
     */
    public IdxIndex() {
        t2imap = new Object2IntOpenHashMap<>();
        t2imap.defaultReturnValue(-1);
        i2tmap = new ArrayList<>();
    }

    /**
     * Adds an element to the structure.
     *
     * @param t element to be added
     * @return the index of the element
     */
    public int add(T t) {
        int idx = t2imap.getInt(t);
        if (idx == -1) {
            idx = i2tmap.size();
            t2imap.put(t, idx);
            i2tmap.add(t);
            return idx;
        } else {
            return idx;
        }
    }

    /**
     * Gets the index of the element.
     *
     * @param t element
     * @return index of the element
     */
    public int get(T t) {
        return t2imap.getInt(t);
    }

    /**
     * Gets the element assigned to the index.
     *
     * @param idx index
     * @return the element whose index is idx
     */
    public T get(int idx) {
        return i2tmap.get(idx);
    }

    /**
     * Checks whether the structure contains this element.
     *
     * @param t element
     * @return does the structure contain this element?
     */
    public boolean containsId(T t) {
        return t2imap.containsKey(t);
    }

    /**
     * Returns the number of stored elements.
     *
     * @return the number of stored elements
     */
    public int size() {
        return t2imap.size();
    }

    /**
     * Returns a stream of the elements stored in the structure.
     *
     * @return a stream of the elements stored in the structure
     */
    public Stream<T> getIds() {
        return t2imap.keySet().stream();
    }
}
