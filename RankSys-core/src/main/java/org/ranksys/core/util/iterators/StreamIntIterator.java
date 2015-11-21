/*
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.iterators;

import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.PrimitiveIterator.OfInt;
import java.util.stream.IntStream;

/**
 * Int primitive iterator wrapping an IntStream.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class StreamIntIterator implements IntIterator {

    private final OfInt it;

    public StreamIntIterator(IntStream stream) {
        this.it = stream.iterator();
    }

    @Override
    public int nextInt() {
        return it.nextInt();
    }

    @Override
    public int skip(int n) {
        int j = 0;
        while (it.hasNext() && j < n) {
            it.nextInt();
            j++;
        }
        
        return j;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Integer next() {
        return it.next();
    }

}
