/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.AbstractFastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.function.Function;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import org.ranksys.core.preference.IdPref;

/**
 * Extends AbstractFastPreferenceData and implements the data access stream-based methods using the iterator-based ones. Avoids duplicating code where iterator-based methods are preferred.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <U> user type
 * @param <I> item type
 */
public abstract class IteratorsAbstractFastPreferenceData<U, I> extends AbstractFastPreferenceData<U, I> {

    /**
     * Constructor with default IdxPref to IdPref converter.
     *
     * @param userIndex user index
     * @param itemIndex item index
     */
    public IteratorsAbstractFastPreferenceData(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex) {
        super(userIndex, itemIndex);
    }

    /**
     * Constructor with custom IdxPref to IdPref converter.
     *
     * @param userIndex user index
     * @param itemIndex item index
     * @param uPrefFun  user IdxPref to IdPref converter
     * @param iPrefFun  item IdxPref to IdPref converter
     */
    public IteratorsAbstractFastPreferenceData(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex, Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun) {
        super(userIndex, itemIndex, uPrefFun, iPrefFun);
    }

    @Override
    public Stream<? extends IdxPref> getUidxPreferences(int uidx) {
        return getPreferences(numItems(uidx), getUidxIidxs(uidx), getUidxVs(uidx));
    }

    @Override
    public Stream<? extends IdxPref> getIidxPreferences(int iidx) {
        return getPreferences(numUsers(iidx), getIidxUidxs(iidx), getIidxVs(iidx));
    }

    /**
     * Converts the int and double iterators to a stream of IdxPref.
     *
     * @param n length of iterators
     * @param idxs iterator of user/item indices
     * @param vs interator of user/item values
     * @return stream of IdxPref
     */
    protected Stream<IdxPref> getPreferences(int n, IntIterator idxs, DoubleIterator vs) {
        return range(0, n).mapToObj(i -> new IdxPref(idxs.nextInt(), vs.nextDouble()));
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return true;
    }

}
