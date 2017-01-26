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
 * Integer compression codec.
 * 
 * Compression of arrays of integers into more compact
 * representations of them.
 * <br>
 * If you use this code, please cite the following papers:
 * <ul>
 * <li>Vargas, S., Macdonald, C., Ounis, I. (2015). Analysing Compression Techniques for In-Memory Collaborative Filtering. In Poster Proceedings of the 9th ACM Conference on Recommender Systems. <a href="http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf">http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf</a>.</li>
 * <li>Catena, M., Macdonald, C., Ounis, I. (2014). On Inverted Index Compression for Search Engine Efficiency. In ECIR (pp. 359–371). doi:10.1007/978-3-319-06028-6_30</li>
 * </ul>
 * The code that reproduces the results of the RecSys 2015 poster by Vargas et al. in a separated project: <a href="http://github.com/saulvargas/recsys2015">http://github.com/saulvargas/recsys2015</a>
 * <br>
 * The search index compression technologies of the ECIR paper by Catena et al. is part of the Terrier IR Platform: <a href="http://terrier.org/docs/v4.0/compression.html">http://terrier.org/docs/v4.0/compression.html</a>.
 * 
 * @param <T> type of the output of the compression
 * 
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
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
    T co(int[] in, int offset, int len);

    /**
     * Decompress.
     *
     * @param t compressed  representation of an array.
     * @param out array where to write the compressed.
     * @param outOffset offset of out
     * @param len number of integers that need to be decompressed
     * @return currently not used
     */
    int dec(T t, int[] out, int outOffset, int len);

    /**
     * Returns statistics of the CODEC in terms of total input/output bytes
     * processed.
     *
     * @return statistics of input/output bytes.
     */
    long[] stats();
    
    /**
     * Resets the collection of statistics.
     */
    void reset();
    
    /**
     * Returns if the CODEC is integrated, i.e. requires that the input is a
     * list of sorted integers (no use of d-gaps required).
     *
     * @return true is the CODEC is integrated, false otherwise
     */
    boolean isIntegrated();
}
