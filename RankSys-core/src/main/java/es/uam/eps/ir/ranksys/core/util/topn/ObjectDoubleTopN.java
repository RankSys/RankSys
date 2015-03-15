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

import java.util.Map;

/**
 * Bounded min-heap to keep just the top-n greatest object-double pairs according to the value of the double.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <T> type of the object
 */
public class ObjectDoubleTopN<T> extends AbstractTopN<Map.Entry<T, Double>> {

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

    @Override
    public Map.Entry<T, Double> get(int i) {
        return new Entry<>(keys[i], values[i]);
    }

    /**
     * Gets the object from the pair at the i-th position in the heap.
     *
     * @param i index
     * @return object at position i-th
     */
    public T getObjectAt(int i) {
        return keys[i];
    }

    /**
     * Gets the double from the pair at the i-th position in the heap.
     *
     * @param i index
     * @return double at position i-th
     */
    public double getDoubleAt(int i) {
        return values[i];
    }

    /**
     * Tries to add an object-double pair to the heap.
     *
     * @param object object to be added
     * @param value double to be added
     * @return true if the pair was added to the heap, false otherwise
     */
    public boolean add(T object, double value) {
        return add(new Entry<>(object, value));
    }

    @Override
    protected void set(int i, Map.Entry<T, Double> e) {
        keys[i] = e.getKey();
        if (e instanceof Entry) {
            values[i] = ((Entry<T>) e).getDoubleValue();
        } else {
            values[i] = e.getValue();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected int compare(int i, Map.Entry<T, Double> e) {
        double v;
        if (e instanceof Entry) {
            v = ((Entry<T>) e).getDoubleValue();
        } else {
            v = e.getValue();
        }

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

    /**
     * Entry for ObjectDoubleTopN
     *
     * @param <T> object type in the key
     */
    public static class Entry<T> implements Map.Entry<T, Double> {

        private final T k;
        private final double v;

        /**
         * Constructor
         *
         * @param k object as key
         * @param v double as value
         */
        public Entry(T k, double v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public T getKey() {
            return k;
        }

        /**
         * Returns the primitive double in the value.
         *
         * @return primitive double in the value
         */
        public double getDoubleValue() {
            return v;
        }

        @Override
        public Double getValue() {
            return v;
        }

        @Override
        public Double setValue(Double value) {
            throw new UnsupportedOperationException();
        }
    }
}
