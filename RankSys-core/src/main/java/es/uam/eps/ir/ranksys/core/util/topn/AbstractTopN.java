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

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Bounded min-heap to keep just the top-n greatest objects.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <T> type of the objects in the heap
 */
public abstract class AbstractTopN<T> extends AbstractCollection<T> {

    /**
     * Maximum capacity of the heap.
     */
    protected int capacity;

    /**
     * Current size of the heap.
     */
    protected int size;

    /**
     * Constructor
     *
     * @param capacity maximum capacity of the heap
     */
    @SuppressWarnings("unchecked")
    public AbstractTopN(int capacity) {
        this.capacity = capacity;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }
    
    /**
     * Returns the element at the top of the heap.
     *
     * @return the element at the top of the heap
     */
    public T peek() {
        return get(0);
    }

    @Override
    public boolean add(T elem) {
        if (size < capacity) {
            int i = size;
            int j = parent(i);
            set(i, elem);
            while (i > 0 && compare(j, i) > 0) {
                swap(i, j);
                i = j;
                j = parent(i);
            }
            size++;

            return true;
        } else {
            if (compare(0, elem) >= 0) {
                return false;
            }
            int i = 0;
            int j = minChild(i);
            set(i, elem);
            while (j != -1 && compare(i, j) > 0) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }

            return true;
        }
    }

    /**
     * Sorts the heap in inverse order (from smallest to greatest).
     */
    public void sort() {
        int origSize = size;
        int origCapacity = capacity;
        capacity = size;
        while (size > 0) {
            swap(0, size - 1);
            size--;
            capacity--;
            int i = 0;
            int j = minChild(i);
            while (j != -1 && compare(i, j) > 0) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }
        }
        size = origSize;
        capacity = origCapacity;
        reverse();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public T next() {
                return get(i++);
            }
        };
    }

    /**
     * Iterates over the elements of the heap in reverse order.
     *
     * @return an iterator in reverse order
     */
    public Iterator<T> reverseIterator() {
        return new Iterator<T>() {
            private int i = size - 1;

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public T next() {
                return get(i--);
            }
        };
    }
    
    /**
     * Creates a stream in reverse order.
     *
     * @return stream in reverse order
     */
    public Stream<T> reverseStream() {
        return StreamSupport.stream(Spliterators.spliterator(reverseIterator(), size(), 0), false);
    }

    /**
     * Returns the i-th element in the heap.
     * 
     * @param i index of the element
     * @return the element in the heap
     */
    protected abstract T get(int i);

    /**
     * Sets the element in the i-th position of the heap.
     *
     * @param i index
     * @param e element
     */
    protected abstract void set(int i, T e);
    
    /**
     * Compares the i-th element of the heap with the element
     *
     * @param i index
     * @param e element
     * @return a number less than, equal to or greater than zero if the i-th element in the heap is smaller, equal or greater than the element.
     */
    protected abstract int compare(int i, T e);
    
    /**
     * Compares the i-th element of the heap with the j-th element in the heap.
     *
     * @param i index
     * @param j index
     * @return a number less than, equal to or greater than zero if the i-th element in the heap is smaller, equal or greater than j-th in the heap.
     */
    protected abstract int compare(int i, int j);
    
    /**
     * Swaps the elements in the i-th and j-th element in the heap.
     *
     * @param i index
     * @param j index
     */
    protected abstract void swap(int i, int j);

    private void reverse() {
        int i = 0;
        int j = size - 1;
        while (j > i) {
            swap(i, j);
            j--;
            i++;
        }
    }

    private int minChild(int i) {
        int l = left(i);
        int r = right(i);

        if (l >= capacity) {
            return -1;
        }

        if (r >= size) {
            return l;
        }

        return compare(l, r) < 0 ? l : r;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return (i + 1) * 2 - 1;
    }

    private int right(int i) {
        return (i + 1) * 2;
    }

}
