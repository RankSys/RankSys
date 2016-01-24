/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.preference;

import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import es.uam.eps.ir.ranksys.core.index.UserIndex;
import java.util.stream.Stream;

/**
 * User-item preference data required for recommendation algorithms.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface PreferenceData<U, I> extends UserIndex<U>, ItemIndex<I> {

    /**
     * Returns the number of users with preferences.
     *
     * @return number of users with preferences
     */
    public int numUsersWithPreferences();

    /**
     * Returns the number of items with preferences.
     *
     * @return number of items with preferences
     */
    public int numItemsWithPreferences();

    /**
     * Returns the number of users with preference for item i
     *
     * @param i item
     * @return number of users with preferences for item i
     */
    public int numUsers(I i);

    /**
     * Returns the number of items with preference for user u
     *
     * @param u user
     * @return number of items with preferences for user u
     */
    public int numItems(U u);

    /**
     * Returns the total number of preferences
     *
     * @return total number of preferences
     */
    public int numPreferences();

    /**
     * Returns a stream of the users with preferences
     *
     * @return stream of users with preferences
     */
    public Stream<U> getUsersWithPreferences();

    /**
     * Returns a stream of the items with preferences
     *
     * @return stream of items with preferences
     */
    public Stream<I> getItemsWithPreferences();

    /**
     * Returns a stream of the preferences of the user
     *
     * @param u user
     * @return stream of preferences
     */
    public Stream<? extends IdPref<I>> getUserPreferences(U u);

    /**
     * Returns a stream of the preferences of the item
     *
     * @param i item
     * @return stream of preferences
     */
    public Stream<? extends IdPref<U>> getItemPreferences(I i);
}
