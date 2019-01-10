/*
 *  Copyright (C) 2016 Information Retrieval Group at Universidad Aut�noma
 *  de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.updateable;

import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Preference data that allows updating over time
 * @author Javier Sanz-Cruzado Puig
 * @param <U> Type of the users
 * @param <I> Type of the items
 */
public interface Updateable<U,I>
{
    /**
     * Updates the preference data given a set of preferences.
     * It does not add new users/items. Tuples with non-existing
     * users/items will be ignored.
     * @param tuples the tuples.
     */
    public void update(Stream<Tuple3<U,I,Double>> tuples);
    
    /**
     * Updates an individual preference.
     * @param u user
     * @param i item
     * @param val preference value
     */
    public void update(U u, I i, double val);
    /**
     * Updates the preference data given a set of preferences to delete
     * @param tuples the tuples
     */
    public void updateDelete(Stream<Tuple2<U,I>> tuples);
    
    /**
     * Deletes an invidivual preference
     * @param u user
     * @param i item
     */
    public void updateDelete(U u, I i);
    
    /**
     * Adds a user
     * @param u user
     */
    public void updateAddUser(U u);
    
    /**
     * Adds an item
     * @param i item
     */
    public void updateAddItem(I i);
}
