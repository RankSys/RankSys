/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item.neighborhood;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.nn.item.sim.ItemSimilarity;
import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.ThresholdNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.TopKNeighborhood;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.stream.Stream;

import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Static methods for constructing item neighborhoods.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class ItemNeighborhoods {

    /**
     * Top-k item neighborhood.
     *
     * @param similarity item similarity
     * @param k          number of highest similar items to consider neighbors
     * @param <I>        item type
     * @return item neighborhood
     */
    public static <I> ItemNeighborhood<I> topK(ItemSimilarity<I> similarity, int k) {
        return new ItemNeighborhood<>(similarity, new TopKNeighborhood(similarity.similarity(), k));
    }

    /**
     * Threshold item neighborhood.
     *
     * @param similarity item similarity
     * @param threshold  similarities above this threshold are considered neighbors
     * @param <I>        item type
     * @return item neighborhood
     */
    public static <I> ItemNeighborhood<I> threshold(ItemSimilarity<I> similarity, double threshold) {
        return new ItemNeighborhood<>(similarity, new ThresholdNeighborhood(similarity.similarity(), threshold));
    }

    /**
     * Cached item neighborhood. Calculates and then caches the neighborhood.
     *
     * @param neighborhood item neighborhood
     * @param <I>          item type
     * @return item neighborhood
     */
    public static <I> ItemNeighborhood<I> cached(ItemNeighborhood<I> neighborhood) {
        return new ItemNeighborhood<>(neighborhood, new CachedNeighborhood(neighborhood.numItems(), neighborhood.neighborhood()));
    }

    /**
     * Cached item neighborhood. Caches a pre-calculated set of neighborhoods.
     *
     * @param items         item index
     * @param neighborhoods pre-calculated neighborhoods
     * @param <I>           item type
     * @return item neighborhood
     */
    public static <I> ItemNeighborhood<I> cached(FastItemIndex<I> items, Stream<Tuple2<I, Stream<Tuple2od<I>>>> neighborhoods) {
        return new ItemNeighborhood<>(items, new CachedNeighborhood(items.numItems(), neighborhoods
                .map(t -> tuple(items.item2iidx(t.v1), t.v2.map(items::item2iidx)))));
    }

}
