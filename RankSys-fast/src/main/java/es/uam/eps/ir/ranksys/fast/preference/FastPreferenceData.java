/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast version of PreferenceData, where preferences for users and items are
 * stored internally by the indexes provided by FastUserIndex and FastItemIndex.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface FastPreferenceData<U, I> extends PreferenceData<U, I>, FastUserIndex<U>, FastItemIndex<I> {

    /**
     * Returns the number of users who have a preference for the item.
     *
     * @param iidx item index
     * @return number of users who have a preference for the item
     */
    int numUsers(int iidx);

    /**
     * Returns the number of items for which the user has preference for.
     *
     * @param uidx user index
     * @return number of items for which the user has preference for
     */
    int numItems(int uidx);
    
    /**
     * Returns a stream of user indexes who have preferences for items.
     *
     * @return a stream of user indexes who have preferences for items
     */
    IntStream getUidxWithPreferences();
    
    /**
     * Returns a stream of item indexes for which users have preferences.
     *
     * @return a stream of item indexes for which users have preferences
     */
    IntStream getIidxWithPreferences();
    
    /**
     * Gets the preferences of a user.
     *
     * @param uidx user index
     * @return preferences of the user
     */
    Stream<? extends IdxPref> getUidxPreferences(int uidx);
    
    /**
     * Gets the preferences of an item.
     *
     * @param iidx item index
     * @return preferences of the item
     */
    Stream<? extends IdxPref> getIidxPreferences(int iidx);
    
    
    /**
     * Returns the item idxs of the preferences of a user.
     *
     * @param uidx user index
     * @return iterator of the idxs of the items
     */
    IntIterator getUidxIidxs(final int uidx);

    /**
     * Returns the item values of the preferences of a user.
     *
     * @param uidx user index
     * @return iterator of the values of the items
     */
    DoubleIterator getUidxVs(final int uidx);

    /**
     * Returns the user idxs of the preferences for an item.
     *
     * @param iidx item index
     * @return iterator of the idxs of the users.
     */
    IntIterator getIidxUidxs(final int iidx);

    /**
     * Returns the user values of the preferences for an item.
     *
     * @param iidx item index
     * @return iterator of the values of the users
     */
    DoubleIterator getIidxVs(final int iidx);

    /**
     * Use methods returning IntIterator or DoubleIterator over streams of
     * IdxPref?
     * 
     * @return yes/no
     */
    boolean useIteratorsPreferentially();
}
