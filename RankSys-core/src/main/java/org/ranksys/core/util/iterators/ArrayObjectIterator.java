/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.iterators;

import it.unimi.dsi.fastutil.objects.ObjectIterator;

/**
 * Array-backed iterator over objects.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class ArrayObjectIterator<V> implements ObjectIterator<V> {

    private final V[] a;
    private int i = 0;

    /**
     * Constructor.
     *
     * @param a array to iterate over
     */
    public ArrayObjectIterator(V[] a) {
        this.a = a;
    }

    @Override
    public V next() {
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
}
