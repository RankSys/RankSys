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
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
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
 * @param <V> type of other information about preferences
 */
public interface FastPreferenceData<U, I, V> extends PreferenceData<U, I, V>, FastUserIndex<U>, FastItemIndex<I> {

    /**
     * Returns the number of users who have a preference for the item.
     *
     * @param iidx item index
     * @return number of users who have a preference for the item
     */
    public int numUsers(int iidx);

    /**
     * Returns the number of items for which the user has preference for.
     *
     * @param uidx user index
     * @return number of items for which the user has preference for
     */
    public int numItems(int uidx);
    
    /**
     * Returns a stream of user indexes who have preferences for items.
     *
     * @return a stream of user indexes who have preferences for items
     */
    public IntStream getUidxWithPreferences();
    
    /**
     * Returns a stream of item indexes for which users have preferences.
     *
     * @return a stream of item indexes for which users have preferences
     */
    public IntStream getIidxWithPreferences();
    
    /**
     * Gets the preferences of a user.
     *
     * @param uidx user index
     * @return preferences of the user
     */
    public Stream<IdxPref<V>> getUidxPreferences(int uidx);

    /**
     * Gets the preferences of an item.
     *
     * @param iidx item index
     * @return preferences of the item
     */
    public Stream<IdxPref<V>> getIidxPreferences(int iidx);
}
