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
package es.uam.eps.ir.ranksys.core.util.topn;

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
    public T get(int i) {
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
