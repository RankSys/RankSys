/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.topn;

import java.util.Comparator;

/**
 * Bounded min-heap to keep just the top-n greatest objects.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <T> type of the objects in the heap
 */
public class TopN<T> extends AbstractTopN<T> {

    private final Comparator<T> cmp;
    private final T[] heap;

    /**
     * Constructor that assumes that the class T implements the Comparable interface.
     *
     * @param capacity maximum capacity of the heap
     */
    @SuppressWarnings("unchecked")
    public TopN(int capacity) {
        super(capacity);
        cmp = (T elem1, T elem2) -> ((Comparable<T>) elem1).compareTo(elem2);
        heap = (T[]) new Object[capacity];
    }

    /**
     * Constructor in which the comparator is specified.
     *
     * @param capacity maximum capacity of the heap
     * @param cmp comparator
     */
    @SuppressWarnings("unchecked")
    public TopN(int capacity, Comparator<T> cmp) {
        super(capacity);
        this.cmp = cmp;
        heap = (T[]) new Object[capacity];
    }

    @Override
    protected T get(int i) {
        return heap[i];
    }

    @Override
    protected void set(int i, T e) {
        heap[i] = e;
    }

    @Override
    protected int compare(int i, T e) {
        return cmp.compare(heap[i], e);
    }
    
    @Override
    protected int compare(int i, int j) {
        return cmp.compare(heap[i], heap[j]);
    }

    @Override
    protected void swap(int i, int j) {
        T elem = heap[i];
        heap[i] = heap[j];
        heap[j] = elem;
    }

}
