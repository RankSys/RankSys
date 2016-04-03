/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.utils.topn;

import es.uam.eps.ir.ranksys.core.util.topn.AbstractTopN;
import org.ranksys.core.util.tuples.Tuple2id;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Bounded min-heap to keep just the top-n greatest integer-double pairs according to the value of the double.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class IntDoubleTopN extends AbstractTopN<Tuple2id> {

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
        return add(tuple(key, value));
    }

    @Override
    protected Tuple2id get(int i) {
        return tuple(keys[i], values[i]);
    }

    @Override
    protected void set(int i, Tuple2id e) {
        keys[i] = e.v1;
        values[i] = e.v2;
    }

    @Override
    protected int compare(int i, Tuple2id e) {
        int k = e.v1;
        double v = e.v2;

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
