/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.user.neighborhood;

import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;
import java.util.stream.Stream;
import static org.jooq.lambda.tuple.Tuple.tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Cached user similarity. See {@link CachedNeighborhood}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public class CachedUserNeighborhood<U> extends UserNeighborhood<U> {

    /**
     * Constructor that calculates and caches user neighborhoods.
     *
     * @param neighborhood user neighborhood to be cached
     */
    public CachedUserNeighborhood(UserNeighborhood<U> neighborhood) {
        super(neighborhood, new CachedNeighborhood(neighborhood.numUsers(), neighborhood));
    }

    /**
     * Constructor that caches a stream of previously calculated neighborhoods.
     *
     * @param uIndex fast user index
     * @param neighborhoods stream of already calculated neighborhoods
     */
    public CachedUserNeighborhood(FastUserIndex<U> uIndex, Stream<Tuple2<U, Stream<Tuple2od<U>>>> neighborhoods) {
        super(uIndex, new CachedNeighborhood(uIndex.numUsers(), neighborhoods
                .map(t -> tuple(t.v1, t.v2.map(uIndex::user2uidx)))
                .map(uIndex::user2uidx)));
    }
}
