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
import org.ranksys.core.util.iterators.StreamDoubleIterator;
import org.ranksys.core.util.iterators.StreamIntIterator;

/**
 * Extends AbstractFastPreferenceData and implements the data access iterator-based methods
 * using the stream-based ones. Avoids duplicating code where stream-based methods
 * are preferred.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public abstract class StreamsAbstractFastPreferenceData<U, I> extends AbstractFastPreferenceData<U, I> {

    public StreamsAbstractFastPreferenceData(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex) {
        super(userIndex, itemIndex);
    }

    @Override
    public IntIterator getUidxIidxs(int uidx) {
        return new StreamIntIterator(getUidxPreferences(uidx).mapToInt(IdxPref::v1));
    }

    @Override
    public DoubleIterator getUidxVs(int uidx) {
        return new StreamDoubleIterator(getUidxPreferences(uidx).mapToDouble(IdxPref::v2));
    }

    @Override
    public IntIterator getIidxUidxs(int iidx) {
        return new StreamIntIterator(getIidxPreferences(iidx).mapToInt(IdxPref::v1));
    }

    @Override
    public DoubleIterator getIidxVs(int iidx) {
        return new StreamDoubleIterator(getIidxPreferences(iidx).mapToDouble(IdxPref::v2));
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return false;
    }

}
