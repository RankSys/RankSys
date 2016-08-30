/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.preference;

import java.util.stream.Stream;

/**
 * Concatenation of two PreferenceData's
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ConcatPreferenceData<U, I> implements PreferenceData<U, I> {

    private final PreferenceData<U, I> d1;
    private final PreferenceData<U, I> d2;

    /**
     * Constructor.
     *
     * @param d1 recommender data
     * @param d2 recommender data
     */
    public ConcatPreferenceData(PreferenceData<U, I> d1, PreferenceData<U, I> d2) {
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
    public Stream<IdPref<I>> getUserPreferences(U u) {
        return Stream.concat(d1.getUserPreferences(u), d2.getUserPreferences(u));
    }

    @Override
    public Stream<IdPref<U>> getItemPreferences(I i) {
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
