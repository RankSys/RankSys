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
import es.uam.eps.ir.ranksys.fast.IdxObject;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import static java.util.stream.Stream.empty;

/**
 * Cached neighborhood. Stores user neighborhoods.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class CachedNeighborhood implements Neighborhood {

    private final IntArrayList[] idxla;
    private final DoubleArrayList[] simla;

    /**
     * Constructor that calculates and caches neighborhoods.
     *
     * @param n number of users/items
     * @param neighborhood generic neighborhood to be cached
     */
    public CachedNeighborhood(int n, Neighborhood neighborhood) {

        this.idxla = new IntArrayList[n];
        this.simla = new DoubleArrayList[n];

        range(0, n).parallel().forEach(idx -> {
            IntArrayList idxl = new IntArrayList();
            DoubleArrayList siml = new DoubleArrayList();
            neighborhood.getNeighbors(idx).forEach(is -> {
                idxl.add(is.idx);
                siml.add(is.v);
            });
            idxla[idx] = idxl;
            simla[idx] = siml;
        });
    }

    /**
     * Constructor that caches a stream of previously calculated neighborhoods.
     *
     * @param n number of users/items
     * @param neighborhoods stream of already calculated neighborhoods
     */
    public CachedNeighborhood(int n, Stream<IdxObject<Stream<IdxDouble>>> neighborhoods) {

        this.idxla = new IntArrayList[n];
        this.simla = new DoubleArrayList[n];

        neighborhoods.forEach(un -> {
            int idx = un.idx;
            IntArrayList idxl = new IntArrayList();
            DoubleArrayList siml = new DoubleArrayList();
            un.v.forEach(is -> {
                idxl.add(is.idx);
                siml.add(is.v);
            });
            idxla[idx] = idxl;
            simla[idx] = siml;
        });
    }

    @Override
    public Stream<IdxDouble> getNeighbors(int idx) {
        if (idx < 0) {
            return empty();
        }
        IntArrayList idxl = idxla[idx];
        DoubleArrayList siml = simla[idx];
        if (idxl == null || siml == null) {
            return empty();
        }
        return range(0, idxl.size()).mapToObj(i -> new IdxDouble(idxl.getInt(i), siml.getDouble(i)));
    }

}
