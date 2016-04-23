/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.feature;

import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import org.ranksys.fast.utils.map.Int2ObjectDirectMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.core.util.tuples.Tuple2io;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Simple implementation of FastFeatureData backed by nested lists.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information about item-feature pairs
 */
public class SimpleFastFeatureData<I, F, V> extends AbstractFastFeatureData<I, F, V> {

    private final Int2ObjectDirectMap<List<Tuple2io<V>>> iidxList;
    private final Int2ObjectDirectMap<List<Tuple2io<V>>> fidxList;

    /**
     * Constructor.
     *
     * @param iidxList list of lists of item-feature pairs by item index
     * @param fidxList list of lists of item-feature pairs by feature index
     * @param ii item index
     * @param fi feature index
     */
    protected SimpleFastFeatureData(Int2ObjectDirectMap<List<Tuple2io<V>>> iidxList, Int2ObjectDirectMap<List<Tuple2io<V>>> fidxList, FastItemIndex<I> ii, FastFeatureIndex<F> fi) {
        super(ii, fi);
        this.iidxList = iidxList;
        this.fidxList = fidxList;
    }

    @Override
    public Stream<Tuple2io<V>> getIidxFeatures(int iidx) {
        return iidxList.getOrDefault(iidx, Collections.emptyList()).stream();
    }

    @Override
    public Stream<Tuple2io<V>> getFidxItems(int fidx) {
        return fidxList.getOrDefault(fidx, Collections.emptyList()).stream();
    }

    @Override
    public int numItems(int fidx) {
        return fidxList.getOrDefault(fidx, Collections.emptyList()).size();
    }

    @Override
    public int numFeatures(int iidx) {
        return iidxList.getOrDefault(iidx, Collections.emptyList()).size();
    }

    @Override
    public IntStream getIidxWithFeatures() {
        return iidxList.keyStream();
    }

    @Override
    public IntStream getFidxWithItems() {
        return fidxList.keyStream();
    }

    @Override
    public int numItemsWithFeatures() {
        return iidxList.size();
    }

    @Override
    public int numFeaturesWithItems() {
        return fidxList.size();
    }

    public static <I, F, V> SimpleFastFeatureData<I, F, V> load(Stream<Tuple3<I, F, V>> tuples, FastItemIndex<I> iIndex, FastFeatureIndex<F> fIndex) {
        Int2ObjectDirectMap<List<Tuple2io<V>>> iidxList = new Int2ObjectDirectMap<>(0, iIndex.numItems() - 1);
        Int2ObjectDirectMap<List<Tuple2io<V>>> fidxList = new Int2ObjectDirectMap<>(0, fIndex.numFeatures() - 1);

        tuples.forEach(t -> {
            int iidx = iIndex.item2iidx(t.v1);
            int fidx = fIndex.feature2fidx(t.v2);

            iidxList.computeIfAbsent(iidx, iidx_ -> new ArrayList<>()).add(tuple(fidx, t.v3));
            fidxList.computeIfAbsent(fidx, fidx_ -> new ArrayList<>()).add(tuple(iidx, t.v3));
        });

        return new SimpleFastFeatureData<>(iidxList, fidxList, iIndex, fIndex);
    }

}
