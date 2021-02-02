/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util;

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
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
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
     * Removes an element from the structure.
     * @param t element to be removed
     * @return the previous index of the element.
     */
    public int remove(T t) {
        int idx = t2imap.getInt(t);
        if(idx == -1)
        {
            return -1;
        }
        else
        {
            for(int i = idx + 1; i < this.size(); ++i)
            {
                T elem = i2tmap.get(i);
                t2imap.put(elem, i - 1);
            }
            t2imap.removeInt(t);
            i2tmap.remove(idx);
        }
        
        return idx;
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
