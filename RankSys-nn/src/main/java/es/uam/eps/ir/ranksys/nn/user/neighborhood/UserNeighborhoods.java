/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.user.neighborhood;

import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.ThresholdNeighborhood;
import es.uam.eps.ir.ranksys.nn.neighborhood.TopKNeighborhood;
import es.uam.eps.ir.ranksys.nn.user.sim.UserSimilarity;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.stream.Stream;

import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Static methods for constructing user neighborhoods.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class UserNeighborhoods {

    /**
     * Top-k user neighborhood.
     *
     * @param similarity user similarity
     * @param k          number of highest similar users to consider neighbors
     * @param <U>        user type
     * @return user neighborhood
     */
    public static <U> UserNeighborhood<U> topK(UserSimilarity<U> similarity, int k) {
        return new UserNeighborhood<>(similarity, new TopKNeighborhood(similarity.similarity(), k));
    }

    /**
     * Threshold user neighborhood.
     *
     * @param similarity user similarity
     * @param threshold  similarities above this threshold are considered neighbors
     * @param <U>        user type
     * @return user neighborhood
     */
    public static <U> UserNeighborhood<U> threshold(UserSimilarity<U> similarity, double threshold) {
        return new UserNeighborhood<>(similarity, new ThresholdNeighborhood(similarity.similarity(), threshold));
    }

    /**
     * Cached user neighborhood. Calculates and then caches the neighborhood.
     *
     * @param neighborhood user neighborhood
     * @param <U>          user type
     * @return user neighborhood
     */
    public static <U> UserNeighborhood<U> cached(UserNeighborhood<U> neighborhood) {
        return new UserNeighborhood<>(neighborhood, new CachedNeighborhood(neighborhood.numUsers(), neighborhood.neighborhood()));
    }

    /**
     * Cached user neighborhood. Caches a pre-calculated set of neighborhoods.
     *
     * @param users         user index
     * @param neighborhoods pre-calculated neighborhoods
     * @param <U>           user type
     * @return user neighborhood
     */
    public static <U> UserNeighborhood<U> cached(FastUserIndex<U> users, Stream<Tuple2<U, Stream<Tuple2od<U>>>> neighborhoods) {
        return new UserNeighborhood<>(users, new CachedNeighborhood(users.numUsers(), neighborhoods
                .map(t -> tuple(users.user2uidx(t.v1), t.v2.map(users::user2uidx)))));
    }
}
