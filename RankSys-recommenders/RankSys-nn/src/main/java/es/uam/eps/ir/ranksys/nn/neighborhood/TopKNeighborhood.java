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

import es.uam.eps.ir.ranksys.core.util.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.nn.sim.Similarity;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import static java.util.stream.Stream.builder;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class TopKNeighborhood implements Neighborhood {

    private final Similarity sim;
    private final Neighborhood superNeighborhood;
    private final int k;

    public TopKNeighborhood(Similarity sim, int k) {
        this.sim = sim;
        this.superNeighborhood = null;
        this.k = k;
    }

    public TopKNeighborhood(Neighborhood superNeighborhood, int k) {
        this.sim = null;
        this.superNeighborhood = superNeighborhood;
        this.k = k;
    }

    @Override
    public Stream<IdxDouble> getNeighbors(int idx) {

        IntDoubleTopN topN = new IntDoubleTopN(k);

        Stream<IdxDouble> candidates;
        if (sim != null) {
            candidates = sim.similarElems(idx);
        } else {
            candidates = superNeighborhood.getNeighbors(idx);
        }

        candidates.forEach(is -> topN.add(is.idx, is.v));

        Builder<IdxDouble> builder = builder();
        for (int i = 0; i < topN.size(); i++) {
            builder.accept(new IdxDouble(topN.getIntAt(i), topN.getDoubleAt(i)));
        }
        
        return builder.build();
    }
}
