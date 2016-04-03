/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.neighborhood;

import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Generic fast neighborhood. Implementing classes of this interface are under the
 * hood of user and item neighborhoods.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface Neighborhood {
    
    /**
     * Returns the neighborhood of a user/index.
     *
     * @param idx user/index whose neighborhood is calculated
     * @return stream of user/item-similarity pairs.
     */
    public Stream<Tuple2id> getNeighbors(int idx);
}
