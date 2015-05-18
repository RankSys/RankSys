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
