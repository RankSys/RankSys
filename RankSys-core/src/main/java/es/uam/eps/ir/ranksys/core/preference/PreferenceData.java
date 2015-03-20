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
package es.uam.eps.ir.ranksys.core.preference;

import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import es.uam.eps.ir.ranksys.core.index.UserIndex;
import java.util.stream.Stream;

/**
 * User-item preference data required for recommendation algorithms
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <O> type of other information for users and items
 */
public interface PreferenceData<U, I, O> extends UserIndex<U>, ItemIndex<I> {

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
    public Stream<IdPref<I, O>> getUserPreferences(U u);

    /**
     * Returns a stream of the preferences of the item
     *
     * @param i item
     * @return stream of preferences
     */
    public Stream<IdPref<U, O>> getItemPreferences(I i);
}
