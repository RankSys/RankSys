/*
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.iterators;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.PrimitiveIterator.OfDouble;
import java.util.stream.DoubleStream;

/**
 * Double primitive iterator wrapping a DoubleStream.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class StreamDoubleIterator implements DoubleIterator {

    private final OfDouble it;

    public StreamDoubleIterator(DoubleStream stream) {
        this.it = stream.iterator();
    }

    @Override
    public double nextDouble() {
        return it.nextDouble();
    }

    @Override
    public int skip(int n) {
        int j = 0;
        while (it.hasNext() && j < n) {
            it.nextDouble();
            j++;
        }
        
        return j;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Double next() {
        return it.next();
    }

}
