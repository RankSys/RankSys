/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.util.topn;

import it.unimi.dsi.fastutil.objects.AbstractObject2DoubleMap.BasicEntry;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry;

/**
 * Bounded min-heap to keep just the top-n greatest object-double pairs according to the value of the double.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <T> type of the object
 */
public class ObjectDoubleTopN<T> extends AbstractTopN<Entry<T>> {

    private final T[] keys;
    private final double[] values;

    /**
     * Constructor.
     *
     * @param capacity maximum capacity of the heap
     */
    @SuppressWarnings("unchecked")
    public ObjectDoubleTopN(int capacity) {
        super(capacity);
        keys = (T[]) new Object[capacity];
        values = new double[capacity];
    }

    /**
     * Tries to add an object-double pair to the heap.
     *
     * @param object object to be added
     * @param value double to be added
     * @return true if the pair was added to the heap, false otherwise
     */
    public boolean add(T object, double value) {
        return add(new BasicEntry<>(object, value));
    }

    @Override
    protected Entry<T> get(int i) {
        return new BasicEntry<>(keys[i], values[i]);
    }

    @Override
    protected void set(int i, Entry<T> e) {
        keys[i] = e.getKey();
        values[i] = e.getDoubleValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected int compare(int i, Entry<T> e) {
        double v = e.getDoubleValue();

        int c = Double.compare(values[i], v);
        if (c != 0) {
            return c;
        } else {
            c = ((Comparable<T>) keys[i]).compareTo(e.getKey());
            return c;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected int compare(int i, int j) {
        int c = Double.compare(values[i], values[j]);
        if (c != 0) {
            return c;
        } else {
            c = ((Comparable<T>) keys[i]).compareTo(keys[j]);
            return c;
        }
    }

    @Override
    protected void swap(int i, int j) {
        T k = keys[i];
        keys[i] = keys[j];
        keys[j] = k;
        double v = values[i];
        values[i] = values[j];
        values[j] = v;
    }

}
