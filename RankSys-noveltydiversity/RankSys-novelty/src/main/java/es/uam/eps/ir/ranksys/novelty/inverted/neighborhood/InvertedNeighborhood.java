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
package es.uam.eps.ir.ranksys.novelty.inverted.neighborhood;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.IdxObject;
import es.uam.eps.ir.ranksys.nn.neighborhood.Neighborhood;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Inverted neighborhood.
 * 
 * S. Vargas and P. Castells. Improving sales diversity by recommending
 * users to items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class InvertedNeighborhood implements Neighborhood {

    private final IntArrayList[] idxla;
    private final DoubleArrayList[] simla;

    /**
     * Constructor.
     *
     * @param n number of users/items
     * @param neighborhood original neighborhood to be inverted
     * @param filter filter to determine the users that require an inverted
     * neighborhood
     */
    public InvertedNeighborhood(int n, Neighborhood neighborhood, IntPredicate filter) {
        this.idxla = new IntArrayList[n];
        this.simla = new DoubleArrayList[n];

        IntStream.range(0, n).parallel().filter(filter).forEach(idx -> {
            this.idxla[idx] = new IntArrayList();
            this.simla[idx] = new DoubleArrayList();
        });

        IntStream.range(0, n).parallel().mapToObj(idx -> {
            return new IdxObject<>(idx, neighborhood.getNeighbors(idx));
        }).forEachOrdered(in -> {
            int idx = in.idx;
            in.v.forEach(is -> {
                if (this.idxla[is.idx] != null) {
                    this.idxla[is.idx].add(idx);
                    this.simla[is.idx].add(is.v);
                }
            });
        });
    }

    @Override
    public Stream<IdxDouble> getNeighbors(int idx) {
        IntArrayList idxl = idxla[idx];
        DoubleArrayList siml = simla[idx];
        return IntStream.range(0, idxl.size()).mapToObj(i -> new IdxDouble(idxl.getInt(i), siml.getDouble(i)));
    }

}
