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

import java.util.stream.Stream;

/**
 * Concatenation of two PreferenceData's
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <O> type of other information for users and items
 */
public class ConcatPreferenceData<U, I, O> implements PreferenceData<U, I, O> {

    private final PreferenceData<U, I, O> d1;
    private final PreferenceData<U, I, O> d2;

    /**
     * Constructor.
     *
     * @param d1 recommender data
     * @param d2 recommender data
     */
    public ConcatPreferenceData(PreferenceData<U, I, O> d1, PreferenceData<U, I, O> d2) {
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public boolean containsUser(U u) {
        return d1.containsUser(u) || d2.containsUser(u);
    }

    @Override
    public int numUsers() {
        return (int) getAllUsers().count();
    }

    @Override
    public int numUsers(I i) {
        return d1.numUsers(i) + d2.numUsers(i);
    }

    @Override
    public boolean containsItem(I i) {
        return d1.containsItem(i) || d2.containsItem(i);
    }

    @Override
    public int numItems() {
        return (int) getAllItems().count();
    }

    @Override
    public int numItems(U u) {
        return d1.numItems(u) + d2.numItems(u);
    }

    @Override
    public int numPreferences() {
        return d1.numPreferences() + d2.numPreferences();
    }

    @Override
    public Stream<U> getAllUsers() {
        return Stream.concat(d1.getAllUsers(), d2.getAllUsers()).distinct();
    }

    @Override
    public Stream<I> getAllItems() {
        return Stream.concat(d1.getAllItems(), d2.getAllItems()).distinct();
    }

    @Override
    public Stream<IdPref<I, O>> getUserPreferences(U u) {
        return Stream.concat(d1.getUserPreferences(u), d2.getUserPreferences(u));
    }

    @Override
    public Stream<IdPref<U, O>> getItemPreferences(I i) {
        return Stream.concat(d1.getItemPreferences(i), d2.getItemPreferences(i));
    }

    @Override
    public int numUsersWithPreferences() {
        return (int) getUsersWithPreferences().count();
    }

    @Override
    public int numItemsWithPreferences() {
        return (int) getItemsWithPreferences().count();
    }

    @Override
    public Stream<U> getUsersWithPreferences() {
        return Stream.concat(d1.getUsersWithPreferences(), d2.getUsersWithPreferences()).distinct();
    }

    @Override
    public Stream<I> getItemsWithPreferences() {
        return Stream.concat(d1.getItemsWithPreferences(), d2.getItemsWithPreferences()).distinct();
    }
}
