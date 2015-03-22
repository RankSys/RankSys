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
package es.uam.eps.ir.ranksys.fast.utils.topn;

import es.uam.eps.ir.ranksys.core.util.topn.AbstractTopN;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap.Entry;
import it.unimi.dsi.fastutil.ints.AbstractInt2DoubleMap.BasicEntry;

/**
 * Bounded min-heap to keep just the top-n greatest integer-double pairs according to the value of the double.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class IntDoubleTopN extends AbstractTopN<Entry> {

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

    /**
     * Tries to add an integer-double pair to the heap.
     *
     * @param key integer to be added
     * @param value double to be added
     * @return true if the pair was added to the heap, false otherwise
     */
    public boolean add(int key, double value) {
        return add(new BasicEntry(key, value));
    }

    @Override
    protected Entry get(int i) {
        return new BasicEntry(keys[i], values[i]);
    }

    @Override
    protected void set(int i, Entry e) {
        keys[i] = e.getIntKey();
        values[i] = e.getDoubleValue();
    }

    @Override
    protected int compare(int i, Entry e) {
        int k = e.getIntKey();
        double v = e.getDoubleValue();

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

}
