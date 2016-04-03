/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.neighborhood;

import es.uam.eps.ir.ranksys.nn.sim.Similarity;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Threshold neighborhood. Items with a similarity above a threshold are kept
 * as neighbors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class ThresholdNeighborhood implements Neighborhood {

    private final Similarity sim;
    private final double threshold;

    /**
     * Constructor.
     *
     * @param sim similarity
     * @param threshold minimum value to be considered as neighbor
     */
    public ThresholdNeighborhood(Similarity sim, double threshold) {
        this.sim = sim;
        this.threshold = threshold;
    }

    /**
     * Returns the neighborhood of a user/index.
     *
     * @param idx user/index whose neighborhood is calculated
     * @return stream of user/item-similarity pairs.
     */
    @Override
    public Stream<Tuple2id> getNeighbors(int idx) {
        return sim.similarElems(idx).filter(is -> is.v2 > threshold);
    }

}
