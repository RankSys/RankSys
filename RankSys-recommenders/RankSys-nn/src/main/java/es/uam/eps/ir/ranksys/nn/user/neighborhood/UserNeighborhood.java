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
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.nn.neighborhood.Neighborhood;
import java.util.stream.Stream;

/**
 * User neighborhood. Wraps a generic neighborhood and a fast user index.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public abstract class UserNeighborhood<U> implements Neighborhood, FastUserIndex<U> {

    /**
     * Fast user index.
     */
    protected final FastUserIndex<U> uIndex;

    /**
     * Generic fast neighborhood.
     */
    protected final Neighborhood neighborhood;

    /**
     * Constructor
     *
     * @param uIndex fast user index
     * @param neighborhood generic fast neighborhood
     */
    public UserNeighborhood(FastUserIndex<U> uIndex, Neighborhood neighborhood) {
        this.uIndex = uIndex;
        this.neighborhood = neighborhood;
    }

    @Override
    public int numUsers() {
        return uIndex.numUsers();
    }

    @Override
    public int user2uidx(U u) {
        return uIndex.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uIndex.uidx2user(uidx);
    }

    @Override
    public Stream<IdxDouble> getNeighbors(int idx) {
        return neighborhood.getNeighbors(idx);
    }

    /**
     * Returns a stream of user neighbors
     *
     * @param u user whose neighborhood is returned
     * @return a stream of user-score pairs
     */
    public Stream<IdDouble<U>> getNeighbors(U u) {
        return getNeighbors(user2uidx(u))
                .map(uv -> new IdDouble<>(uidx2user(uv.idx), uv.v));
    }
}
