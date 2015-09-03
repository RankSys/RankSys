/* 
 * Copyright (C) 2015 RankSys http://ranksys.github.io
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
package com.github.ranksys.compression.util;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;

/**
 * Array-backed iterator over primitive doubles.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class ArrayDoubleIterator implements DoubleIterator {

    private final double[] a;
    private int i = 0;

    /**
     * Constructor.
     *
     * @param a array to iterate over
     */
    public ArrayDoubleIterator(double[] a) {
        this.a = a;
    }

    @Override
    public double nextDouble() {
        return a[i++];
    }

    @Override
    public int skip(int n) {
        throw new UnsupportedOperationException("TO DO");
    }

    @Override
    public boolean hasNext() {
        return i < a.length;
    }

    @Override
    public Double next() {
        throw new UnsupportedOperationException("use nextDouble() instead");
    }
}
