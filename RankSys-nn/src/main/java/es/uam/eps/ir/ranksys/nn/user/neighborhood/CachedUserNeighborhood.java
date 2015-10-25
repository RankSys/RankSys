/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.user.neighborhood;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.IdxObject;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;
import java.util.stream.Stream;

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
    public CachedUserNeighborhood(FastUserIndex<U> uIndex, Stream<IdObject<U, Stream<IdDouble<U>>>> neighborhoods) {
        super(uIndex, new CachedNeighborhood(uIndex.numUsers(), neighborhoods.map(un -> new IdxObject<>(uIndex.user2uidx(un.id), un.v.map(vs -> new IdxDouble(uIndex.user2uidx(vs.id), vs.v))))));
    }
}
