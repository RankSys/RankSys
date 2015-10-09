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
package org.ranksys.compression.codecs;

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
public interface CODEC<T> {

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
