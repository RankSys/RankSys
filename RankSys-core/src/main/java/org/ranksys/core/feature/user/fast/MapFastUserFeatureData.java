/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.feature.user.fast;

import org.jooq.lambda.tuple.Tuple3;

import org.ranksys.core.index.fast.FastFeatureIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.util.tuples.Tuple2io;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Simple implementation of FastUserFeatureData backed by maps.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users
 * @param <F> type of the features
 * @param <V> type of the information about user-feature pairs
 */
public class MapFastUserFeatureData<U,F,V> extends AbstractFastUserFeatureData<U,F,V>
{
    /**
     * Map relating users to feature-value pairs.
     */
    Map<Integer, Map<Integer, V>> uidxMap;
    /**
     * Map relating feature to user-value pairs.
     */
    Map<Integer, Map<Integer, V>> fidxMap;

    /**
     * Constructor.
     *
     * @param uidxMap map containing the feature-value pairs for each users.
     * @param fidxMap map containing the user-value pairs for each feature.
     * @param ui user index
     * @param fi feature index
     */
    protected MapFastUserFeatureData(Map<Integer, Map<Integer, V>> uidxMap, Map<Integer, Map<Integer, V>> fidxMap, FastUserIndex<U> ui, FastFeatureIndex<F> fi)
    {
        super(ui, fi);
        this.uidxMap = uidxMap;
        this.fidxMap = fidxMap;

    }

    @Override
    public Stream<Tuple2io<V>> getUidxFeatures(int uidx)
    {
        if(!uidxMap.containsKey(uidx))
        {
            return Stream.empty();
        }
        else
        {
            return uidxMap.get(uidx).entrySet().stream().map(entry -> new Tuple2io<>(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public Stream<Tuple2io<V>> getFidxUsers(int fidx)
    {
        if(!fidxMap.containsKey(fidx))
        {
            return Stream.empty();
        }
        else
        {
            return fidxMap.get(fidx).entrySet().stream().map(entry -> new Tuple2io<>(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public int numUsers(int fidx)
    {
        return (fidxMap.containsKey(fidx)) ? fidxMap.get(fidx).size() : 0;
    }

    @Override
    public int numFeatures(int uidx)
    {
        return (uidxMap.containsKey(uidx)) ? uidxMap.get(uidx).size() : 0;
    }

    @Override
    public IntStream getUidxWithFeatures()
    {
        return uidxMap.keySet().stream().sorted().mapToInt(x->x);
    }

    @Override
    public IntStream getFidxWithUsers()
    {
        return fidxMap.keySet().stream().sorted().mapToInt(x->x);
    }

    @Override
    public int numUsersWithFeatures()
    {
        return uidxMap.keySet().size();
    }

    @Override
    public int numFeaturesWithUsers()
    {
        return fidxMap.keySet().size();
    }

    /**
     * Loads a MapFastUserFeatureData by processing a stream of user-feature-value triples.
     *
     * @param <U> type of users
     * @param <F> type of feats
     * @param <V> type of value
     * @param tuples user-feature-value triples
     * @param uIndex user index
     * @param fIndex feat index
     * @return a MapFastUserFeatureData containing the information from the input triples
     */
    public static <U, F, V> MapFastUserFeatureData<U, F, V> load(Stream<Tuple3<U, F, V>> tuples, FastUserIndex<U> uIndex, FastFeatureIndex<F> fIndex) {

        Map<Integer, Map<Integer, V>> uidxMap = new HashMap<>();
        Map<Integer, Map<Integer, V>> fidxMap = new HashMap<>();

        tuples.forEach(t ->
        {
            int uidx = uIndex.user2uidx(t.v1);
            int fidx = fIndex.feature2fidx(t.v2);

            if (uidx == -1 || fidx == -1) {
                return;
            }

            Map<Integer, V> uMap;
            if(uidxMap.containsKey(uidx))
            {
                uMap = new HashMap<>();
                uidxMap.put(uidx, uMap);
            }
            else
            {
                uMap = uidxMap.get(uidx);
            }

            uMap.put(fidx, t.v3);

            Map<Integer, V> fMap;
            if(fidxMap.containsKey(fidx))
            {
                fMap = new HashMap<>();
                fidxMap.put(fidx, fMap);
            }
            else
            {
                fMap = fidxMap.get(fidx);
            }

            fMap.put(uidx, t.v3);
        });

        return new MapFastUserFeatureData<>(uidxMap, fidxMap, uIndex, fIndex);
    }
}
