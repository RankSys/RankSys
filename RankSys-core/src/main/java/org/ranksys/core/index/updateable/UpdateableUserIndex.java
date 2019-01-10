/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index.updateable;

import java.util.stream.Stream;
import org.ranksys.core.index.UserIndex;

/**
 * Updateable index for a set of users.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public interface UpdateableUserIndex<U> extends UserIndex<U>
{
    /**
     * Adds a user to the index.
     * @param u the user.
     * @return the identifier of the new user.
     */
    public int addUser(U u);
    
    /**
     * Removes a user from the index.
     * @param u the user to 
     * @return the old identifier of the user.
     */
    //public int removeUser(U u);
    
    /**
     * Adds a set of users to the index
     * @param users a stream containing the users to add.
     */
    public default void addUsers(Stream<U> users)
    {
        users.forEach(u -> this.addUser(u));
    }
}
