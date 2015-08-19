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
package es.uam.eps.ir.ranksys.nn.neighborhood;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.nn.sim.Similarity;
import java.util.stream.Stream;

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
    public Stream<IdxDouble> getNeighbors(int idx) {

        IntDoubleTopN topN = new IntDoubleTopN(k);
        sim.similarElems(idx).forEach(is -> topN.add(is.idx, is.v));

        return topN.stream().map(e -> new IdxDouble(e));
    }
}
