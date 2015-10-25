/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generic abstract CODEC for statistics collection.
 *
 * @param <T> type of the output of the compression
 * 
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public abstract class AbstractCODEC<T> implements CODEC<T> {

    private final AtomicLong totalBytesIn;
    private final AtomicLong totalBytesOut;

    /**
     * Constructor.
     */
    public AbstractCODEC() {
        this.totalBytesIn = new AtomicLong(0L);
        this.totalBytesOut = new AtomicLong(0L);
    }

    /**
     * Adds the input/output byes of a compression.
     *
     * @param bytesIn bytes read
     * @param bytesOut bytes of the compression
     */
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
