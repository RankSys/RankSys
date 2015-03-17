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
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class UserNeighborhood<U> implements Neighborhood, FastUserIndex<U> {

    protected final FastUserIndex<U> indexedUser;
    protected final Neighborhood neighborhood;

    public UserNeighborhood(FastUserIndex<U> indexedUser, Neighborhood neighborhood) {
        this.indexedUser = indexedUser;
        this.neighborhood = neighborhood;
    }

    @Override
    public int numUsers() {
        return indexedUser.numUsers();
    }

    @Override
    public int user2uidx(U u) {
        return indexedUser.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return indexedUser.uidx2user(uidx);
    }

    @Override
    public Stream<IdxDouble> getNeighbors(int idx) {
        return neighborhood.getNeighbors(idx);
    }

    public Stream<IdDouble<U>> getNeighbors(U u) {
        return getNeighbors(user2uidx(u))
                .map(uv -> new IdDouble<>(uidx2user(uv.idx), uv.v));
    }
}
