/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.nn.sim;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;

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
    public Stream<IdxDouble> similarElems(int idx);
}
