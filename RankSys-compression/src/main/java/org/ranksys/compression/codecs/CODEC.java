/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs;

import java.io.Serializable;

/**
 * Integer compression technique.
 * 
 * Compression of arrays of integers into more compact
 * representations of them.
 *
 * @param <T> type of the output of the compression
 * 
 * @author Saúl Vargas (saul.vargas@glasgow.ac.uk)
 */
public interface CODEC<T> extends Serializable {

    /**
     * Compress.
     *
     * @param in array to be compressed.
     * @param offset offset of in
     * @param len number of integers to be compressed
     * @return compressed representation of the array
     */
    public T co(int[] in, int offset, int len);

    /**
     * Decompress.
     *
     * @param t compressed  representation of an array.
     * @param out array where to write the compressed.
     * @param outOffset offset of out
     * @param len number of integers that need to be decompressed
     * @return currently not used
     */
    public int dec(T t, int[] out, int outOffset, int len);

    /**
     * Returns statistics of the CODEC in terms of total input/output bytes
     * processed.
     *
     * @return statistics of input/output bytes.
     */
    public long[] stats();
    
    /**
     * Resets the collection of statistics.
     */
    public void reset();
    
    /**
     * Returns if the CODEC is integrated, i.e. requires that the input is a
     * list of sorted integers (no use of d-gaps required).
     *
     * @return true is the CODEC is integrated, false otherwise
     */
    public boolean isIntegrated();
}
