/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.inverted.neighborhood;

import es.uam.eps.ir.ranksys.nn.neighborhood.Neighborhood;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;
import static org.ranksys.core.util.tuples.Tuples.tuple;

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
            return tuple(idx, neighborhood.getNeighbors(idx));
        }).forEachOrdered(in -> {
            int idx = in.v1;
            in.v2.forEach(is -> {
                if (this.idxla[is.v1] != null) {
                    this.idxla[is.v1].add(idx);
                    this.simla[is.v1].add(is.v2);
                }
            });
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
        IntArrayList idxl = idxla[idx];
        DoubleArrayList siml = simla[idx];
        return IntStream.range(0, idxl.size()).mapToObj(i -> tuple(idxl.getInt(i), siml.getDouble(i)));
    }

}
