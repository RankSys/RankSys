/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.neighborhood;

import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.nn.sim.Similarity;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Top-K neighborhood. It keeps the k most similar users/items as neighbors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class TopKNeighborhood implements Neighborhood {

    private final Similarity sim;
    private final int k;

    /**
     * Constructor.
     *
     * @param sim similarity
     * @param k maximum size of neighborhood
     */
    public TopKNeighborhood(Similarity sim, int k) {
        this.sim = sim;
        this.k = k;
    }

    /**
     * Returns the neighborhood of a user/index.
     *
     * @param idx user/index whose neighborhood is calculated
     * @return stream of user/item-similarity pairs.
     */
    @Override
    public Stream<Tuple2id> getNeighbors(int idx) {

        IntDoubleTopN topN = new IntDoubleTopN(k);
        sim.similarElems(idx).forEach(topN::add);

        return topN.stream();
    }
}
