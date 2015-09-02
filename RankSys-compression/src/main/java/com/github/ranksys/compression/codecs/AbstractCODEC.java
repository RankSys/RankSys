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
package com.github.ranksys.compression.codecs;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public abstract class AbstractCODEC<T> implements CODEC<T> {

    private final AtomicLong totalBytesIn;
    private final AtomicLong totalBytesOut;

    public AbstractCODEC() {
        this.totalBytesIn = new AtomicLong(0L);
        this.totalBytesOut = new AtomicLong(0L);
    }

    protected void add(long bytesIn, long bytesOut) {
        totalBytesIn.addAndGet(bytesIn);
        totalBytesOut.addAndGet(bytesOut);
    }

    @Override
    public long[] stats() {
        return new long[]{totalBytesIn.get(), totalBytesOut.get()};
    }

    @Override
    public void reset() {
        totalBytesIn.set(0);
        totalBytesOut.set(0);
    }

}
