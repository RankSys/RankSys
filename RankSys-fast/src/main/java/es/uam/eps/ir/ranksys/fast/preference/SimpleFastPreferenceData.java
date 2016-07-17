/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Comparator.comparingInt;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jooq.lambda.function.Function4;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.ranksys.fast.preference.FastPointWisePreferenceData;
import org.ranksys.fast.preference.StreamsAbstractFastPreferenceData;
import org.ranksys.fast.utils.map.Int2ObjectDirectMap;

/**
 * Simple implementation of FastPreferenceData backed by nested lists.
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @author Saúl Vargas (saul.vargas@uam.es)
 */


public class SimpleFastPreferenceData<U, I> extends StreamsAbstractFastPreferenceData<U, I> implements FastPointWisePreferenceData<U, I>, Serializable {

    private final int numPreferences;
    private final Int2ObjectDirectMap<List<IdxPref>> uidxList;
    private final Int2ObjectDirectMap<List<IdxPref>> iidxList;

    /**
     * Constructor.
     *
     * @param numPreferences number of total preferences
     * @param uidxList list of lists of preferences by user index
     * @param iidxList list of lists of preferences by item index
     * @param uIndex user index
     * @param iIndex item index
     */
    protected SimpleFastPreferenceData(int numPreferences,
            Int2ObjectDirectMap<List<IdxPref>> uidxList, Int2ObjectDirectMap<List<IdxPref>> iidxList, 
            FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        this(numPreferences, uidxList, iidxList, uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    protected SimpleFastPreferenceData(int numPreferences, 
            Int2ObjectDirectMap<List<IdxPref>> uidxList, Int2ObjectDirectMap<List<IdxPref>> iidxList,
            FastUserIndex<U> uIndex, FastItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun) {
        super(uIndex, iIndex, uPrefFun, iPrefFun);
        this.numPreferences = numPreferences;
        this.uidxList = uidxList;
        this.iidxList = iidxList;

        uidxList.valueStream().parallel()
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
        iidxList.valueStream().parallel()
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
    }

    @Override
    public int numUsers(int iidx) {
        return iidxList.getOrDefault(iidx, Collections.emptyList()).size();
    }

    @Override
    public int numItems(int uidx) {
        return uidxList.getOrDefault(uidx, Collections.emptyList()).size();
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(int uidx) {
        return uidxList.getOrDefault(uidx, Collections.emptyList()).stream();
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(int iidx) {
        return iidxList.getOrDefault(iidx, Collections.emptyList()).stream();
    }

    @Override
    public int numPreferences() {
        return numPreferences;
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return uidxList.keyStream();
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return iidxList.keyStream();
    }

    @Override
    public int numUsersWithPreferences() {
        return uidxList.size();
    }

    @Override
    public int numItemsWithPreferences() {
        return iidxList.size();
    }

    @Override
    public Optional<IdxPref> getPreference(int uidx, int iidx) {
        List<IdxPref> uList = uidxList.getOrDefault(uidx, Collections.emptyList());

        int low = 0;
        int high = uList.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            IdxPref p = uList.get(mid);
            int cmp = Integer.compare(p.v1, iidx);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return Optional.of(p);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<? extends IdPref<I>> getPreference(U u, I i) {
        Optional<? extends IdxPref> pref = getPreference(user2uidx(u), item2iidx(i));

        if (!pref.isPresent()) {
            return Optional.empty();
        } else {
            return Optional.of(uPrefFun.apply(pref.get()));
        }
    }

    public static <U, I> SimpleFastPreferenceData<U, I> load(Stream<Tuple3<U, I, Double>> tuples, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        return load(tuples.map(t -> t.concat((Void) null)),
                (uidx, iidx, v, o) -> new IdxPref(iidx, v),
                (uidx, iidx, v, o) -> new IdxPref(uidx, v),
                uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    public static <U, I, O> SimpleFastPreferenceData<U, I> load(Stream<Tuple4<U, I, Double, O>> tuples,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> uIdxPrefFun,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> iIdxPrefFun,
            FastUserIndex<U> uIndex, FastItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uIdPrefFun,
            Function<IdxPref, IdPref<U>> iIdPrefFun) {
        AtomicInteger numPreferences = new AtomicInteger();
        Int2ObjectDirectMap<List<IdxPref>> uidxList = new Int2ObjectDirectMap<>(0, uIndex.numUsers() - 1);
        Int2ObjectDirectMap<List<IdxPref>> iidxList = new Int2ObjectDirectMap<>(0, iIndex.numItems() - 1);
        IntFunction<List<IdxPref>> newArray = uidx_ -> new ArrayList<>();

        tuples.forEach(t -> {
            int uidx = uIndex.user2uidx(t.v1);
            int iidx = iIndex.item2iidx(t.v2);

            numPreferences.incrementAndGet();
            uidxList.computeIfAbsent(uidx, newArray).add(uIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));
            iidxList.computeIfAbsent(iidx, newArray).add(iIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));
        });

        return new SimpleFastPreferenceData<>(numPreferences.intValue(), uidxList, iidxList, uIndex, iIndex, uIdPrefFun, iIdPrefFun);
    }

}
