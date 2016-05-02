/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.sim;

import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Generic similarity for fast data. This is the interface that is under the
 * hood of the user and item similarities. It is does not need be symmetric.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface Similarity {
    
    /**
     * Returns a function that returns the similarity with the user/item.
     *
     * @param idx index of the user/item
     * @return function that returns the similarity with the index
     */
    public IntToDoubleFunction similarity(int idx);
    
    /**
     * Returns the similarity between two users/items. 
     *
     * @param idx1 index of user/item
     * @param idx2 index of user/item
     * @return similarity between the pair
     */
    public default double similarity(int idx1, int idx2) {
        return similarity(idx1).applyAsDouble(idx2);
    }
    
    /**
     * Returns all the users/items having a similarity greater than 0,
     * together with the value of the similarity.
     *
     * @param idx index of user/item
     * @return stream of index-similarity pairs
     */
    public Stream<Tuple2id> similarElems(int idx);
}
