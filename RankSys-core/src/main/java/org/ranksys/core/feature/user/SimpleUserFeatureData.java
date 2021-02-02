/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.feature.user;

import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.jooq.lambda.tuple.Tuple.tuple;
/**
 * Simple map-based feature data.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users
 * @param <F> type of the features
 * @param <V> type of the information about user-feature pairs
 */
public class SimpleUserFeatureData<U,F,V> implements UserFeatureData<U,F,V>
{
    private final Map<U, List<Tuple2<F, V>>> userMap;
    private final Map<F, List<Tuple2<U, V>>> featMap;

    /**
     * Constructor
     *
     * @param userMap user to features map
     * @param featMap feature to user map
     */
    protected SimpleUserFeatureData(Map<U, List<Tuple2<F, V>>> userMap, Map<F, List<Tuple2<U, V>>> featMap) {
        this.userMap = userMap;
        this.featMap = featMap;
    }

    @Override
    public Stream<F> getAllFeatures() {
        return featMap.keySet().stream();
    }

    @Override
    public Stream<U> getAllUsers() {
        return userMap.keySet().stream();
    }

    @Override
    public Stream<Tuple2<U, V>> getFeatureUsers(F f) {
        return featMap.getOrDefault(f, new ArrayList<>()).stream();
    }

    @Override
    public Stream<Tuple2<F, V>> getUserFeatures(U u) {
        return userMap.getOrDefault(u, new ArrayList<>()).stream();
    }

    @Override
    public boolean containsFeature(F f) {
        return featMap.containsKey(f);
    }

    @Override
    public int numFeatures() {
        return featMap.size();
    }

    @Override
    public int numFeatures(U u) {
        return userMap.getOrDefault(u, new ArrayList<>()).size();
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
    public int numUsers(F f) {
        return featMap.getOrDefault(f, new ArrayList<>()).size();
    }

    @Override
    public int numUsersWithFeatures() {
        return userMap.size();
    }

    @Override
    public int numFeaturesWithUsers() {
        return featMap.size();
    }

    @Override
    public Stream<U> getUsersWithFeatures() {
        return userMap.keySet().stream();
    }

    @Override
    public Stream<F> getFeaturesWithUsers() {
        return featMap.keySet().stream();
    }

    /**
     * Loads an instance of the class from a stream of triples.
     *
     * @param <U> type of user
     * @param <F> type of feat
     * @param <V> type of value
     * @param tuples stream of user-feat-value triples
     * @return a feature data object
     */
    public static <U, F, V> SimpleUserFeatureData<U, F, V> load(Stream<Tuple3<U, F, V>> tuples) {
        Map<U, List<Tuple2<F, V>>> userMap = new HashMap<>();
        Map<F, List<Tuple2<U, V>>> featMap = new HashMap<>();

        tuples.forEach(t -> {
            userMap.computeIfAbsent(t.v1, v1 -> new ArrayList<>()).add(tuple(t.v2, t.v3));
            featMap.computeIfAbsent(t.v2, v2 -> new ArrayList<>()).add(tuple(t.v1, t.v3));
        });

        return new SimpleUserFeatureData<U,F,V>(userMap, featMap);
    }
}
