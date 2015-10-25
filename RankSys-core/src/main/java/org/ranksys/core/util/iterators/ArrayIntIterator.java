/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.iterators;

import it.unimi.dsi.fastutil.ints.IntIterator;

/**
 * Array-backed iterator over primitive integers.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class ArrayIntIterator implements IntIterator {

    private final int[] a;
    private int i = 0;

    /**
     * Constructor.
     *
     * @param a array to iterate over
     */
    public ArrayIntIterator(int[] a) {
        this.a = a;
    }

    @Override
    public int nextInt() {
        return a[i++];
    }

    @Override
    public int skip(int n) {
        int j = 0;
        while (i < a.length && j < n) {
            i++;
            j++;
        }
        
        return j;
    }

    @Override
    public boolean hasNext() {
        return i < a.length;
    }

    @Override
    public Integer next() {
        return nextInt();
    }
}
