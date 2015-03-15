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
 * Bounded min-heap to keep just the top-n greatest integer-double pairs according to the value of the double.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class IntDoubleTopN extends AbstractTopN<Map.Entry<Integer, Double>> {

    private final int[] keys;
    private final double[] values;

    /**
     * Constructor.
     *
     * @param capacity maximum capacity of the heap
     */
    public IntDoubleTopN(int capacity) {
        super(capacity);
        keys = new int[capacity];
        values = new double[capacity];
    }

    @Override
    public Map.Entry<Integer, Double> get(int i) {
        return new Entry(keys[i], values[i]);
    }

    /**
     * Gets the integer from the pair at the i-th position in the heap.
     *
     * @param i index
     * @return integer at position i-th
     */
    public int getIntAt(int i) {
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
     * Tries to add an integer-double pair to the heap.
     *
     * @param key integer to be added
     * @param value double to be added
     * @return true if the pair was added to the heap, false otherwise
     */
    public boolean add(int key, double value) {
        return add(new Entry(key, value));
    }

    @Override
    protected void set(int i, Map.Entry<Integer, Double> e) {
        if (e instanceof Entry) {
            keys[i] = ((Entry) e).getIntKey();
            values[i] = ((Entry) e).getDoubleValue();
        } else {
            keys[i] = e.getKey();
            values[i] = e.getValue();
        }

    }

    @Override
    protected int compare(int i, Map.Entry<Integer, Double> e) {
        int k;
        double v;
        if (e instanceof Entry) {
            k = ((Entry) e).getIntKey();
            v = ((Entry) e).getDoubleValue();
        } else {
            k = e.getKey();
            v = e.getValue();
        }
        
        int c = Double.compare(values[i], v);
        if (c != 0) {
            return c;
        } else {
            c = Integer.compare(keys[i], k);
            return c;
        }
    }

    @Override
    protected int compare(int i, int j) {
        int c = Double.compare(values[i], values[j]);
        if (c != 0) {
            return c;
        } else {
            c = Integer.compare(keys[i], keys[j]);
            return c;
        }
    }

    @Override
    protected void swap(int i, int j) {
        int k = keys[i];
        keys[i] = keys[j];
        keys[j] = k;
        double v = values[i];
        values[i] = values[j];
        values[j] = v;
    }

    /**
     * Entry for IntDoubleTopN.
     *
     */
    public static class Entry implements Map.Entry<Integer, Double> {

        private final int k;
        private final double v;

        /**
         * Constructor.
         *
         * @param k integer as key
         * @param v double as value
         */
        public Entry(int k, double v) {
            this.k = k;
            this.v = v;
        }

        /**
         * Returns the primitive integer in the key.
         *
         * @return primitive integer in the key
         */
        public int getIntKey() {
            return k;
        }

        @Override
        public Integer getKey() {
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
