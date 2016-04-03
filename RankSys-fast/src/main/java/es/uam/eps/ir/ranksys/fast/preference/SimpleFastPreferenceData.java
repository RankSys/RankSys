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
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.io.Serializable;
import java.util.ArrayList;
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
    private final List<List<IdxPref>> uidxList;
    private final List<List<IdxPref>> iidxList;

    /**
     * Constructor.
     *
     * @param numPreferences number of total preferences
     * @param uidxList list of lists of preferences by user index
     * @param iidxList list of lists of preferences by item index
     * @param uIndex user index
     * @param iIndex item index
     */
    protected SimpleFastPreferenceData(int numPreferences, List<List<IdxPref>> uidxList, List<List<IdxPref>> iidxList, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);
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
            uList.add(new IdxPref(iidx, t.v3));

            List<IdxPref> iList = iidxList.get(iidx);
            if (iList == null) {
                iList = new ArrayList<>();
                iidxList.set(iidx, iList);
            }
            iList.add(new IdxPref(uidx, t.v3));
        });

        return new SimpleFastPreferenceData<>(numPreferences.intValue(), uidxList, iidxList, uIndex, iIndex);
    }

}
