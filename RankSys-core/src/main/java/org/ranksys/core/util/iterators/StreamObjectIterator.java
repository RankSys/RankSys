/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.iterators;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * ObjectIterator wrapping a Stream.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class StreamObjectIterator<V> implements ObjectIterator<V> {

    private final Iterator<V> it;

    /**
     * Constructor.
     *
     * @param stream wrapped stream
     */
    public StreamObjectIterator(Stream<V> stream) {
        this.it = stream.iterator();
    }

    @Override
    public V next() {
        return it.next();
    }

    @Override
    public int skip(int n) {
        int j = 0;
        while (it.hasNext() && j < n) {
            it.next();
            j++;
        }
        
        return j;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

}
