/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Simple implementation of FastUserIndex backed by a bi-map IdxIndex
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 */
public class SimpleFastUserIndex<U> implements FastUserIndex<U>, Serializable {

    private final IdxIndex<U> uMap;

    /**
     * Empty constructor: no users.
     */
    protected SimpleFastUserIndex() {
        this.uMap = new IdxIndex<>();
    }

    @Override
    public boolean containsUser(U u) {
        return uMap.containsId(u);
    }

    @Override
    public int numUsers() {
        return uMap.size();
    }

    @Override
    public Stream<U> getAllUsers() {
        return uMap.getIds();
    }

    @Override
    public int user2uidx(U u) {
        return uMap.get(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uMap.get(uidx);
    }

    /**
     * Add a new user to the index. If the user already exists, nothing is done.
     *
     * @param u id of the user
     * @return index of the user
     */
    protected int add(U u) {
        return uMap.add(u);
    }

    /**
     * Creates a user index from a stream of user objects.
     *
     * @param <U> type of the users
     * @param users stream of user objects
     * @return a fast user index
     */
    public static <U> SimpleFastUserIndex<U> load(Stream<U> users) {
        SimpleFastUserIndex<U> userIndex = new SimpleFastUserIndex<>();
        users.forEach(userIndex::add);
        return userIndex;
    }
}
