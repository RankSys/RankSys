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
import org.ranksys.core.util.tuples.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Simple implementation of FastUserFeatureData backed by nested lists.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users
 * @param <F> type of the features
 * @param <V> type of the information about user-feature pairs
 */
public class SimpleFastUserFeatureData<U,F,V> extends AbstractFastUserFeatureData<U,F,V>
{
    private final List<List<Tuple2io<V>>> uidxList;
    private final List<List<Tuple2io<V>>> fidxList;

    /**
     * Constructor.
     *
     * @param uidxList list of lists of user-feature pairs by user index
     * @param fidxList list of lists of user-feature pairs by feature index
     * @param ui user index
     * @param fi feature index
     */
    protected SimpleFastUserFeatureData(List<List<Tuple2io<V>>> uidxList, List<List<Tuple2io<V>>> fidxList, FastUserIndex<U> ui, FastFeatureIndex<F> fi) {
        super(ui, fi);
        this.uidxList = uidxList;
        this.fidxList = fidxList;
    }

    @Override
    public Stream<Tuple2io<V>> getUidxFeatures(int uidx) {
        if (uidxList.get(uidx) == null) {
            return Stream.empty();
        }
        return uidxList.get(uidx).stream();
    }

    @Override
    public Stream<Tuple2io<V>> getFidxUsers(int fidx) {
        if (fidxList.get(fidx) == null) {
            return Stream.empty();
        }
        return fidxList.get(fidx).stream();
    }

    @Override
    public int numUsers(int fidx) {
        if (fidxList.get(fidx) == null) {
            return 0;
        }
        return fidxList.get(fidx).size();
    }

    @Override
    public int numFeatures(int uidx) {
        if (uidxList.get(uidx) == null) {
            return 0;
        }
        return uidxList.get(uidx).size();
    }

    @Override
    public IntStream getUidxWithFeatures() {
        return IntStream.range(0, numUsers())
                .filter(uidx -> uidxList.get(uidx) != null);
    }

    @Override
    public IntStream getFidxWithUsers() {
        return IntStream.range(0, numFeatures())
                .filter(fidx -> fidxList.get(fidx) != null);
    }

    @Override
    public int numUsersWithFeatures() {
        return (int) uidxList.stream()
                .filter(Objects::nonNull).count();
    }

    @Override
    public int numFeaturesWithUsers() {
        return (int) fidxList.stream()
                .filter(Objects::nonNull).count();
    }

    /**
     * Loads a SimpleFastFeatureData by processing a stream of user-feature-value triples.
     *
     * @param <U> type of users
     * @param <F> type of feats
     * @param <V> type of value
     * @param tuples user-feature-value triples
     * @param uIndex user index
     * @param fIndex feat index
     * @return a SimpleFastUserFeatureData containing the information from the input triples
     */
    public static <U, F, V> SimpleFastUserFeatureData<U, F, V> load(Stream<Tuple3<U, F, V>> tuples, FastUserIndex<U> uIndex, FastFeatureIndex<F> fIndex) {

        List<List<Tuple2io<V>>> uidxList = new ArrayList<>();
        for (int uidx = 0; uidx < uIndex.numUsers(); uidx++) {
            uidxList.add(null);
        }

        List<List<Tuple2io<V>>> fidxList = new ArrayList<>();
        for (int fidx = 0; fidx < fIndex.numFeatures(); fidx++) {
            fidxList.add(null);
        }

        tuples.forEach(t -> {
            int uidx = uIndex.user2uidx(t.v1);
            int fidx = fIndex.feature2fidx(t.v2);

            if (uidx == -1 || fidx == -1) {
                return;
            }

            List<Tuple2io<V>> uList = uidxList.get(uidx);
            if (uList == null) {
                uList = new ArrayList<>();
                uidxList.set(uidx, uList);
            }
            uList.add(Tuples.tuple(fidx, t.v3));

            List<Tuple2io<V>> fList = fidxList.get(fidx);
            if (fList == null) {
                fList = new ArrayList<>();
                fidxList.set(fidx, fList);
            }
            fList.add(Tuples.tuple(uidx, t.v3));
        });

        return new SimpleFastUserFeatureData<>(uidxList, fidxList, uIndex, fIndex);
    }
}
