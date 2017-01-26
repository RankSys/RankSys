/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index;

import es.uam.eps.ir.ranksys.core.index.UserIndex;

/**
 * Mutable user index. You can add and remove users.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <U> user type
 */
public interface MutableUserIndex<U> extends UserIndex<U> {

    /**
     * Adds user.
     *
     * @param u id of the user
     * @return true if new user was added, false otherwise
     */
    public boolean addUser(U u);

    /**
     * Removes user.
     *
     * @param u id of the user
     * @return true if user was removed, false otherwise
     */
    public boolean removeUser(U u);
}
