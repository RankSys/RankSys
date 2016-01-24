/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.AbstractFastPreferenceData;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import static java.lang.Integer.compare;
import static java.util.Collections.binarySearch;
import static java.util.Collections.sort;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.context.pref.ContextFastPreferenceData.IdxPrefCtx;
import org.ranksys.core.util.iterators.StreamDoubleIterator;
import org.ranksys.core.util.iterators.StreamIntIterator;

/**
 * Simple implementation of context-aware fast preference data.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * 
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public class SimpleContextFastPreferenceData<U, I, C> extends AbstractFastPreferenceData<U, I> implements ContextFastPreferenceData<U, I, C> {

    private final int numPreferences;
    private final List<List<IdxPrefCtx<C>>> uidxList;
    private final List<List<IdxPrefCtx<C>>> iidxList;
    private final int contextSize;

    /**
     * Constructor.
     *
     * @param numPreferences number of total preferences
     * @param uidxList list of lists of preferences by user index
     * @param iidxList list of lists of preferences by item index
     * @param uIndex user index
     * @param iIndex item index
     * @param contextSize dimensionality of the context
     */
    public SimpleContextFastPreferenceData(int numPreferences, List<List<IdxPrefCtx<C>>> uidxList, List<List<IdxPrefCtx<C>>> iidxList, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, int contextSize) {
        super(uIndex, iIndex);
        this.numPreferences = numPreferences;
        this.uidxList = uidxList;
        this.iidxList = iidxList;
        this.contextSize = contextSize;

        uidxList.stream().filter(list -> list != null).forEach(list -> sort(list, (p1, p2) -> compare(p1.idx, p2.idx)));
        iidxList.stream().filter(list -> list != null).forEach(list -> sort(list, (p1, p2) -> compare(p1.idx, p2.idx)));
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
    public Stream<IdxPrefCtx<C>> getUidxPreferences(int uidx) {
        if (uidxList.get(uidx) == null) {
            return Stream.empty();
        } else {
            return uidxList.get(uidx).stream();
        }
    }

    @Override
    public Stream<IdxPrefCtx<C>> getIidxPreferences(int iidx) {
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
    public Stream<IdPrefCtx<I, C>> getUserPreferences(U u) {
        return getUidxPreferences(user2uidx(u)).map(iv -> new IdPrefCtx<>(iidx2item(iv.idx), iv.v, iv.cs));
    }

    @Override
    public Stream<IdPrefCtx<U, C>> getItemPreferences(I i) {
        return getIidxPreferences(item2iidx(i)).map(uv -> new IdPrefCtx<>(uidx2user(uv.idx), uv.v, uv.cs));
    }

    @Override
    public Optional<IdxPrefCtx<C>> getPreference(int uidx, int iidx) {
        List<IdxPrefCtx<C>> list = uidxList.get(uidx);
        int i = binarySearch(list, new IdxPrefCtx<>(iidx, 0.0, null), (p1, p2) -> compare(p1.idx, p2.idx));
        if (i < 0) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(i));
        }
    }

    @Override
    public int getContextSize() {
        return contextSize;
    }

    @Override
    public IntIterator getUidxIidxs(int uidx) {
        return new StreamIntIterator(getUidxPreferences(uidx).mapToInt(p -> p.idx));
    }

    @Override
    public DoubleIterator getUidxVs(int uidx) {
        return new StreamDoubleIterator(getUidxPreferences(uidx).mapToDouble(p -> p.v));
    }

    @Override
    public IntIterator getIidxUidxs(int iidx) {
        return new StreamIntIterator(getIidxPreferences(iidx).mapToInt(p -> p.idx));
    }

    @Override
    public DoubleIterator getIidxVs(int iidx) {
        return new StreamDoubleIterator(getIidxPreferences(iidx).mapToDouble(p -> p.v));
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return false;
    }

}
