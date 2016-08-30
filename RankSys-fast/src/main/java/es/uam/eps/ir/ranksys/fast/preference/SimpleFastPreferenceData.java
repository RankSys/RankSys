/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import java.io.Serializable;
import java.util.ArrayList;

import static java.util.Comparator.comparingInt;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jooq.lambda.function.Function4;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.ranksys.core.preference.IdPref;
import org.ranksys.fast.preference.FastPointWisePreferenceData;
import org.ranksys.fast.preference.StreamsAbstractFastPreferenceData;

/**
 * Simple implementation of FastPreferenceData backed by nested lists.
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SimpleFastPreferenceData<U, I> extends StreamsAbstractFastPreferenceData<U, I> implements FastPointWisePreferenceData<U, I>, Serializable {

    private final int numPreferences;
    private final List<List<IdxPref>> uidxList;
    private final List<List<IdxPref>> iidxList;

    /**
     * Constructor with default IdxPref to IdPref converter.
     *
     * @param numPreferences number of total preferences
     * @param uidxList list of lists of preferences by user index
     * @param iidxList list of lists of preferences by item index
     * @param uIndex user index
     * @param iIndex item index
     */
    protected SimpleFastPreferenceData(int numPreferences, List<List<IdxPref>> uidxList, List<List<IdxPref>> iidxList,
            FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        this(numPreferences, uidxList, iidxList, uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    /**
     * Constructor with custom IdxPref to IdPref converter.
     *
     * @param numPreferences number of total preferences
     * @param uidxList list of lists of preferences by user index
     * @param iidxList list of lists of preferences by item index
     * @param uIndex user index
     * @param iIndex item index
     * @param uPrefFun user IdxPref to IdPref converter
     * @param iPrefFun item IdxPref to IdPref converter
     */
    protected SimpleFastPreferenceData(int numPreferences, List<List<IdxPref>> uidxList, List<List<IdxPref>> iidxList,
            FastUserIndex<U> uIndex, FastItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun) {
        super(uIndex, iIndex, uPrefFun, iPrefFun);
        this.numPreferences = numPreferences;
        this.uidxList = uidxList;
        this.iidxList = iidxList;

        uidxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
        iidxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
    }

    @Override
    public int numUsers(int iidx) {
        if (iidxList.get(iidx) == null) {
            return 0;
        }
        return iidxList.get(iidx).size();
    }

    @Override
    public int numItems(int uidx) {
        if (uidxList.get(uidx) == null) {
            return 0;
        }
        return uidxList.get(uidx).size();
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(int uidx) {
        if (uidxList.get(uidx) == null) {
            return Stream.empty();
        } else {
            return uidxList.get(uidx).stream();
        }
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(int iidx) {
        if (iidxList.get(iidx) == null) {
            return Stream.empty();
        } else {
            return iidxList.get(iidx).stream();
        }
    }

    @Override
    public int numPreferences() {
        return numPreferences;
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return IntStream.range(0, numUsers())
                .filter(uidx -> uidxList.get(uidx) != null);
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return IntStream.range(0, numItems())
                .filter(iidx -> iidxList.get(iidx) != null);
    }

    @Override
    public int numUsersWithPreferences() {
        return (int) uidxList.stream()
                .filter(iv -> iv != null).count();
    }

    @Override
    public int numItemsWithPreferences() {
        return (int) iidxList.stream()
                .filter(iv -> iv != null).count();
    }

    @Override
    public Optional<IdxPref> getPreference(int uidx, int iidx) {
        List<IdxPref> uList = uidxList.get(uidx);

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

    /**
     * Loads a SimpleFastPreferenceData from a stream of user-item-value triples.
     *
     * @param <U> user type
     * @param <I> item type
     * @param tuples stream of user-item-value triples
     * @param uIndex user index
     * @param iIndex item index
     * @return an instance of SimpleFastPreferenceData containing the data from the input stream
     */
    public static <U, I> SimpleFastPreferenceData<U, I> load(Stream<Tuple3<U, I, Double>> tuples, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        return load(tuples.map(t -> t.concat((Void) null)),
                (uidx, iidx, v, o) -> new IdxPref(iidx, v),
                (uidx, iidx, v, o) -> new IdxPref(uidx, v),
                uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    /**
     * Loads a SimpleFastPreferenceData from a stream of user-item-value-other tuples. It can accomodate other information, thus you need to provide sub-classes of IdxPref IdPref accomodating for this new information.
     *
     * @param <U> user type
     * @param <I> item type
     * @param <O> additional information type
     * @param tuples stream of user-item-value-other tuples
     * @param uIdxPrefFun converts a tuple to a user IdxPref
     * @param iIdxPrefFun converts a tuple to a item IdxPref
     * @param uIndex user index
     * @param iIndex item index
     * @param uIdPrefFun user IdxPref to IdPref converter
     * @param iIdPrefFun item IdxPref to IdPref converter
     * @return an instance of SimpleFastPreferenceData containing the data from the input stream
     */
    public static <U, I, O> SimpleFastPreferenceData<U, I> load(Stream<Tuple4<U, I, Double, O>> tuples,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> uIdxPrefFun,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> iIdxPrefFun,
            FastUserIndex<U> uIndex, FastItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uIdPrefFun,
            Function<IdxPref, IdPref<U>> iIdPrefFun) {
        AtomicInteger numPreferences = new AtomicInteger();

        List<List<IdxPref>> uidxList = new ArrayList<>();
        for (int uidx = 0; uidx < uIndex.numUsers(); uidx++) {
            uidxList.add(null);
        }

        List<List<IdxPref>> iidxList = new ArrayList<>();
        for (int iidx = 0; iidx < iIndex.numItems(); iidx++) {
            iidxList.add(null);
        }

        tuples.forEach(t -> {
            int uidx = uIndex.user2uidx(t.v1);
            int iidx = iIndex.item2iidx(t.v2);

            numPreferences.incrementAndGet();

            List<IdxPref> uList = uidxList.get(uidx);
            if (uList == null) {
                uList = new ArrayList<>();
                uidxList.set(uidx, uList);
            }
            uList.add(uIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));

            List<IdxPref> iList = iidxList.get(iidx);
            if (iList == null) {
                iList = new ArrayList<>();
                iidxList.set(iidx, iList);
            }
            iList.add(iIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));
        });

        return new SimpleFastPreferenceData<>(numPreferences.intValue(), uidxList, iidxList, uIndex, iIndex, uIdPrefFun, iIdPrefFun);
    }

}
