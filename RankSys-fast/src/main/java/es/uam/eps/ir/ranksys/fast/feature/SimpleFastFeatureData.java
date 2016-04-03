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
import java.util.ArrayList;
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

    private final List<List<Tuple2io<V>>> iidxList;
    private final List<List<Tuple2io<V>>> fidxList;

    /**
     * Constructor.
     *
     * @param iidxList list of lists of item-feature pairs by item index
     * @param fidxList list of lists of item-feature pairs by feature index
     * @param ii item index
     * @param fi feature index
     */
    protected SimpleFastFeatureData(List<List<Tuple2io<V>>> iidxList, List<List<Tuple2io<V>>> fidxList, FastItemIndex<I> ii, FastFeatureIndex<F> fi) {
        super(ii, fi);
        this.iidxList = iidxList;
        this.fidxList = fidxList;
    }

    @Override
    public Stream<Tuple2io<V>> getIidxFeatures(int iidx) {
        if (iidxList.get(iidx) == null) {
            return Stream.empty();
        }
        return iidxList.get(iidx).stream();
    }

    @Override
    public Stream<Tuple2io<V>> getFidxItems(int fidx) {
        if (fidxList.get(fidx) == null) {
            return Stream.empty();
        }
        return fidxList.get(fidx).stream();
    }

    @Override
    public int numItems(int fidx) {
        if (fidxList.get(fidx) == null) {
            return 0;
        }
        return fidxList.get(fidx).size();
    }

    @Override
    public int numFeatures(int iidx) {
        if (iidxList.get(iidx) == null) {
            return 0;
        }
        return iidxList.get(iidx).size();
    }

    @Override
    public IntStream getIidxWithFeatures() {
        return IntStream.range(0, numItems())
                .filter(iidx -> iidxList.get(iidx) != null);
    }

    @Override
    public IntStream getFidxWithItems() {
        return IntStream.range(0, numFeatures())
                .filter(fidx -> fidxList.get(fidx) != null);
    }

    @Override
    public int numItemsWithFeatures() {
        return (int) iidxList.stream()
                .filter(iv -> iv != null).count();
    }

    @Override
    public int numFeaturesWithItems() {
        return (int) fidxList.stream()
                .filter(fv -> fv != null).count();
    }

    public static <I, F, V> SimpleFastFeatureData<I, F, V> load(Stream<Tuple3<I, F, V>> tuples, FastItemIndex<I> iIndex, FastFeatureIndex<F> fIndex) {

        List<List<Tuple2io<V>>> iidxList = new ArrayList<>();
        for (int iidx = 0; iidx < iIndex.numItems(); iidx++) {
            iidxList.add(null);
        }

        List<List<Tuple2io<V>>> fidxList = new ArrayList<>();
        for (int fidx = 0; fidx < fIndex.numFeatures(); fidx++) {
            fidxList.add(null);
        }

        tuples.forEach(t -> {
            int iidx = iIndex.item2iidx(t.v1);
            int fidx = fIndex.feature2fidx(t.v2);

            if (iidx == -1 || fidx == -1) {
                return;
            }

            List<Tuple2io<V>> iList = iidxList.get(iidx);
            if (iList == null) {
                iList = new ArrayList<>();
                iidxList.set(iidx, iList);
            }
            iList.add(tuple(fidx, t.v3));

            List<Tuple2io<V>> fList = fidxList.get(fidx);
            if (fList == null) {
                fList = new ArrayList<>();
                fidxList.set(fidx, fList);
            }
            fList.add(tuple(iidx, t.v3));
        });

        return new SimpleFastFeatureData<>(iidxList, fidxList, iIndex, fIndex);
    }

}
