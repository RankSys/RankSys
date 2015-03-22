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
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.core.index.UserIndex;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast version of UserIndex, where users are internally represented with 
 * numerical indices from 0 (inclusive) to the number of indexed users
 * (exclusive).
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public interface FastUserIndex<U> extends UserIndex<U> {

    @Override
    public default boolean containsUser(U u) {
        return user2uidx(u) >= 0;
    }

    @Override
    public default Stream<U> getAllUsers() {
        return getAllUidx().mapToObj(uidx -> uidx2user(uidx));
    }
    
    /**
     * Gets all the indices of the users.
     *
     * @return a stream of indexes of users
     */
    public default IntStream getAllUidx() {
        return IntStream.range(0, numUsers());
    }

    /**
     * Returns the index assigned to the user.
     *
     * @param u user
     * @return the index of the user, or -1 if the user does not exist
     */
    public int user2uidx(U u);

    /**
     * Returns the user represented with the index.
     *
     * @param uidx user index
     * @return the user whose index is uidx
     */
    public U uidx2user(int uidx);

}
