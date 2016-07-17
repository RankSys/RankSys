/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.AbstractFastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.function.Function;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;

/**
 * Extends AbstractFastPreferenceData and implements the data access stream-based methods using the iterator-based ones. Avoids duplicating code where iterator-based methods are preferred.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public abstract class IteratorsAbstractFastPreferenceData<U, I> extends AbstractFastPreferenceData<U, I> {

    public IteratorsAbstractFastPreferenceData(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex) {
        super(userIndex, itemIndex);
    }

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

    protected Stream<IdxPref> getPreferences(int n, IntIterator idxs, DoubleIterator vs) {
        return range(0, n).mapToObj(i -> new IdxPref(idxs.nextInt(), vs.nextDouble()));
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return true;
    }

}
