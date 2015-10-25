/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
