/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.neighborhood;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import static java.util.stream.Stream.empty;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2io;
import static org.ranksys.core.util.tuples.Tuples.tuple;

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
                idxl.add(is.v1);
                siml.add(is.v2);
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
    public CachedNeighborhood(int n, Stream<Tuple2io<Stream<Tuple2id>>> neighborhoods) {

        this.idxla = new IntArrayList[n];
        this.simla = new DoubleArrayList[n];

        neighborhoods.forEach(un -> {
            int idx = un.v1;
            IntArrayList idxl = new IntArrayList();
            DoubleArrayList siml = new DoubleArrayList();
            un.v2.forEach(is -> {
                idxl.add(is.v1);
                siml.add(is.v2);
            });
            idxla[idx] = idxl;
            simla[idx] = siml;
        });
    }

    /**
     * Returns the neighborhood of a user/index.
     *
     * @param idx user/index whose neighborhood is calculated
     * @return stream of user/item-similarity pairs.
     */
    @Override
    public Stream<Tuple2id> getNeighbors(int idx) {
        if (idx < 0) {
            return empty();
        }
        IntArrayList idxl = idxla[idx];
        DoubleArrayList siml = simla[idx];
        if (idxl == null || siml == null) {
            return empty();
        }
        return range(0, idxl.size()).mapToObj(i -> tuple(idxl.getInt(i), siml.getDouble(i)));
    }

}
