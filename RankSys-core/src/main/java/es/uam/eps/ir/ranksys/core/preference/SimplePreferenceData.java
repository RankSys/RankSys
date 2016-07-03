/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.preference;

import org.jooq.lambda.function.Function4;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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
    public Stream<? extends IdPref<I>> getUserPreferences(U u) {
        return userMap.getOrDefault(u, new ArrayList<>()).stream();
    }

    @Override
    public Stream<? extends IdPref<U>> getItemPreferences(I i) {
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
        return load((Stream<Tuple4<U, I, Double, Void>>) tuples.map(t -> t.concat((Void) null)),
                (u, i, v, o) -> new IdPref<>(i, v), (u, i, v, o) -> new IdPref<>(u, v));
    }

    /**
     * Loads an instance of the class from a stream of triples.
     *
     * @param <U> type of user
     * @param <I> type of item
     * @param tuples stream of user-item-value triples
     * @return a preference data object
     */
    public static <U, I, O> SimplePreferenceData<U, I> load(Stream<Tuple4<U, I, Double, O>> tuples,
                                                            Function4<U, I, Double, O, ? extends IdPref<I>> uPrefFun,
                                                            Function4<U, I, Double, O, ? extends IdPref<U>> iPrefFun) {
        AtomicInteger numPreferences = new AtomicInteger(0);
        Map<U, List<IdPref<I>>> userMap = new HashMap<>();
        Map<I, List<IdPref<U>>> itemMap = new HashMap<>();

        tuples.forEach(t -> {
            numPreferences.incrementAndGet();
            userMap.computeIfAbsent(t.v1, v1 -> new ArrayList<>()).add(uPrefFun.apply(t));
            itemMap.computeIfAbsent(t.v2, v2 -> new ArrayList<>()).add(iPrefFun.apply(t));
        });

        return new SimplePreferenceData<>(userMap, itemMap, numPreferences.intValue());
    }
}
