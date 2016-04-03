/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.preference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Simple map-based preference data
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class SimplePreferenceData<U, I> implements PreferenceData<U, I>, Serializable {

    private final Map<U, List<IdPref<I>>> userMap;
    private final Map<I, List<IdPref<U>>> itemMap;
    private final int numPreferences;

    /**
     * Constructor.
     *
     * @param userMap user to preferences map
     * @param itemMap item to preferences map
     * @param numPreferences total number of preferences
     */
    protected SimplePreferenceData(Map<U, List<IdPref<I>>> userMap, Map<I, List<IdPref<U>>> itemMap, int numPreferences) {
        this.userMap = userMap;
        this.itemMap = itemMap;
        this.numPreferences = numPreferences;
    }

    @Override
    public boolean containsUser(U u) {
        return userMap.containsKey(u);
    }

    @Override
    public int numUsers() {
        return userMap.size();
    }

    @Override
    public int numUsers(I i) {
        return itemMap.getOrDefault(i, new ArrayList<>(0)).size();
    }

    @Override
    public boolean containsItem(I i) {
        return itemMap.containsKey(i);
    }

    @Override
    public int numItems() {
        return itemMap.size();
    }

    @Override
    public int numItems(U u) {
        return userMap.getOrDefault(u, new ArrayList<>()).size();
    }

    @Override
    public int numPreferences() {
        return numPreferences;
    }

    @Override
    public Stream<U> getAllUsers() {
        return userMap.keySet().stream();
    }

    @Override
    public Stream<I> getAllItems() {
        return itemMap.keySet().stream();
    }

    @Override
    public Stream<IdPref<I>> getUserPreferences(U u) {
        return userMap.getOrDefault(u, new ArrayList<>()).stream();
    }

    @Override
    public Stream<IdPref<U>> getItemPreferences(I i) {
        return itemMap.getOrDefault(i, new ArrayList<>()).stream();
    }

    @Override
    public int numUsersWithPreferences() {
        return userMap.size();
    }

    @Override
    public int numItemsWithPreferences() {
        return itemMap.size();
    }

    @Override
    public Stream<U> getUsersWithPreferences() {
        return userMap.keySet().stream();
    }

    @Override
    public Stream<I> getItemsWithPreferences() {
        return itemMap.keySet().stream();
    }

    public static <U, I> SimplePreferenceData<U, I> load(Stream<Tuple3<U, I, Double>> tuples) {
        Map<U, List<IdPref<I>>> userMap = new HashMap<>();
        Map<I, List<IdPref<U>>> itemMap = new HashMap<>();
        AtomicInteger numPreferences = new AtomicInteger(0);

        tuples.forEach(t -> {
            numPreferences.incrementAndGet();

            List<IdPref<I>> uList = userMap.get(t.v1);
            if (uList == null) {
                uList = new ArrayList<>();
                userMap.put(t.v1, uList);
            }
            uList.add(new IdPref<>(t.v2, t.v3));

            List<IdPref<U>> iList = itemMap.get(t.v2);
            if (iList == null) {
                iList = new ArrayList<>();
                itemMap.put(t.v2, iList);
            }
            iList.add(new IdPref<>(t.v1, t.v3));
        });

        return new SimplePreferenceData<>(userMap, itemMap, numPreferences.intValue());
    }

}
