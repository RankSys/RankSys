/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.feature.item.fast;

import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.core.index.fast.FastFeatureIndex;
import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.util.tuples.Tuple2io;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Simple implementation of FastItemFeatureData backed by maps.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information about item-feature pairs
 */
public class MapFastItemFeatureData<I,F,V> extends AbstractFastItemFeatureData<I,F,V>
{
    /**
     * Map relating items to feature-value pairs.
     */
    Map<Integer, Map<Integer, V>> iidxMap;
    /**
     * Map relating feature to item-value pairs.
     */
    Map<Integer, Map<Integer, V>> fidxMap;

    /**
     * Constructor.
     *
     * @param iidxMap map containing the feature-value pairs for each item.
     * @param fidxMap map containing the item-value pairs for each feature.
     * @param ii item index
     * @param fi feature index
     */
    protected MapFastItemFeatureData(Map<Integer, Map<Integer, V>> iidxMap, Map<Integer, Map<Integer, V>> fidxMap, FastItemIndex<I> ii, FastFeatureIndex<F> fi)
    {
        super(ii, fi);
        this.iidxMap = iidxMap;
        this.fidxMap = fidxMap;

    }

    @Override
    public Stream<Tuple2io<V>> getIidxFeatures(int iidx)
    {
        if(!iidxMap.containsKey(iidx))
        {
            return Stream.empty();
        }
        else
        {
            return iidxMap.get(iidx).entrySet().stream().map(entry -> new Tuple2io<>(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public Stream<Tuple2io<V>> getFidxItems(int fidx)
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
    public int numItems(int fidx)
    {
        return (fidxMap.containsKey(fidx)) ? fidxMap.get(fidx).size() : 0;
    }

    @Override
    public int numFeatures(int iidx)
    {
        return (iidxMap.containsKey(iidx)) ? iidxMap.get(iidx).size() : 0;
    }

    @Override
    public IntStream getIidxWithFeatures()
    {
        return iidxMap.keySet().stream().sorted().mapToInt(x->x);
    }

    @Override
    public IntStream getFidxWithItems()
    {
        return fidxMap.keySet().stream().sorted().mapToInt(x->x);
    }

    @Override
    public int numItemsWithFeatures()
    {
        return iidxMap.keySet().size();
    }

    @Override
    public int numFeaturesWithItems()
    {
        return fidxMap.keySet().size();
    }

    /**
     * Loads a MapFastItemFeatureData by processing a stream of item-feature-value triples.
     *
     * @param <I> type of items
     * @param <F> type of feats
     * @param <V> type of value
     * @param tuples item-feature-value triples
     * @param iIndex item index
     * @param fIndex feat index
     * @return a MapFastItemFeatureData containing the information from the input triples
     */
    public static <I, F, V> MapFastItemFeatureData<I, F, V> load(Stream<Tuple3<I, F, V>> tuples, FastItemIndex<I> iIndex, FastFeatureIndex<F> fIndex) {

        Map<Integer, Map<Integer, V>> iidxMap = new HashMap<>();
        Map<Integer, Map<Integer, V>> fidxMap = new HashMap<>();

        tuples.forEach(t ->
        {
            int iidx = iIndex.item2iidx(t.v1);
            int fidx = fIndex.feature2fidx(t.v2);

            if (iidx == -1 || fidx == -1) {
                return;
            }

            Map<Integer, V> iMap;
            if(iidxMap.containsKey(iidx))
            {
                iMap = new HashMap<>();
                iidxMap.put(iidx, iMap);
            }
            else
            {
                iMap = iidxMap.get(iidx);
            }

            iMap.put(fidx, t.v3);

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

            fMap.put(iidx, t.v3);
        });

        return new MapFastItemFeatureData<>(iidxMap, fidxMap, iIndex, fIndex);
    }
}
