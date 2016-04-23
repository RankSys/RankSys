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
import org.ranksys.fast.utils.map.Int2ObjectDirectMap;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Comparator.comparingInt;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.fast.preference.FastPointWisePreferenceData;
import org.ranksys.core.util.iterators.StreamDoubleIterator;
import org.ranksys.core.util.iterators.StreamIntIterator;

/**
 * Simple implementation of FastPreferenceData backed by nested lists.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class SimpleFastPreferenceData<U, I> extends AbstractFastPreferenceData<U, I> implements FastPointWisePreferenceData<U, I>, Serializable {

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
    protected SimpleFastPreferenceData(int numPreferences, Int2ObjectDirectMap<List<IdxPref>> uidxList, Int2ObjectDirectMap<List<IdxPref>> iidxList, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);
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
    public IntIterator getUidxIidxs(final int uidx) {
        return new StreamIntIterator(getUidxPreferences(uidx).mapToInt(IdxPref::v1));
    }

    @Override
    public DoubleIterator getUidxVs(final int uidx) {
        return new StreamDoubleIterator(getUidxPreferences(uidx).mapToDouble(IdxPref::v2));
    }

    @Override
    public IntIterator getIidxUidxs(final int iidx) {
        return new StreamIntIterator(getIidxPreferences(iidx).mapToInt(IdxPref::v1));
    }

    @Override
    public DoubleIterator getIidxVs(final int iidx) {
        return new StreamDoubleIterator(getIidxPreferences(iidx).mapToDouble(IdxPref::v2));
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return false;
    }

    public static <U, I> SimpleFastPreferenceData<U, I> load(Stream<Tuple3<U, I, Double>> tuples, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        AtomicInteger numPreferences = new AtomicInteger();
        Int2ObjectDirectMap<List<IdxPref>> uidxList = new Int2ObjectDirectMap<>(0, uIndex.numUsers() - 1);
        Int2ObjectDirectMap<List<IdxPref>> iidxList = new Int2ObjectDirectMap<>(0, iIndex.numItems() - 1);

        tuples.forEach(t -> {
            int uidx = uIndex.user2uidx(t.v1);
            int iidx = iIndex.item2iidx(t.v2);

            numPreferences.incrementAndGet();
            uidxList.computeIfAbsent(uidx, uidx_ -> new ArrayList<>()).add(new IdxPref(iidx, t.v3));
            iidxList.computeIfAbsent(iidx, iidx_ -> new ArrayList<>()).add(new IdxPref(uidx, t.v3));
        });

        return new SimpleFastPreferenceData<>(numPreferences.intValue(), uidxList, iidxList, uIndex, iIndex);
    }

}
