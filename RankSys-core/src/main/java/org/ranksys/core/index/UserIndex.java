/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index;

import java.util.stream.Stream;

/**
 * Index for a set of users.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public interface UserIndex<U> {

    /**
     * Checks whether the index contains a user.
     *
     * @param u user
     * @return true if the index contains the user, false otherwise
     */
    public boolean containsUser(U u);

    /**
     * Counts the number of indexed users.
     *
     * @return the total number of users
     */
    public int numUsers();

    /**
     * Retrieves a stream of the indexed users.
     *
     * @return a stream of all the users
     */
    public Stream<U> getAllUsers();

}
