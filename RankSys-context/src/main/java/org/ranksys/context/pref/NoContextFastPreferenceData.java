/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.fast.preference.FastPointWisePreferenceData;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public class NoContextFastPreferenceData<U, I, C> extends NoContextPreferenceData<U, I, C> implements ContextFastPreferenceData<U, I, C> {

    public NoContextFastPreferenceData(FastPointWisePreferenceData<U, I> preferences, C emptyCtx) {
        super(preferences, emptyCtx);
    }

    @Override
    public Stream<IdxPrefCtx<C>> getUidxPreferences(int uidx) {
        return ((FastPointWisePreferenceData<U, I>) d).getUidxPreferences(uidx).map(p -> new IdxPrefCtx<>(p.idx, p.v, Arrays.asList(emptyCtx)));
    }

    @Override
    public Stream<IdxPrefCtx<C>> getIidxPreferences(int iidx) {
        return ((FastPointWisePreferenceData<U, I>) d).getIidxPreferences(iidx).map(p -> new IdxPrefCtx<>(p.idx, p.v, Arrays.asList(emptyCtx)));
    }

    @Override
    public Optional<IdxPrefCtx<C>> getPreference(int uidx, int iidx) {
        Optional<? extends IdxPref> p = ((FastPointWisePreferenceData<U, I>) d).getPreference(uidx, iidx);
        if (!p.isPresent()) {
            return Optional.empty();
        } else {
            return Optional.of(new IdxPrefCtx<>(p.get().idx, p.get().v, Arrays.asList(emptyCtx)));
        }
    }

    @Override
    public int numUsers(int iidx) {
        return ((FastPointWisePreferenceData<U, I>) d).numUsers(iidx);
    }

    @Override
    public int numItems(int uidx) {
        return ((FastPointWisePreferenceData<U, I>) d).numItems(uidx);
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return ((FastPointWisePreferenceData<U, I>) d).getUidxWithPreferences();
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return ((FastPointWisePreferenceData<U, I>) d).getIidxWithPreferences();
    }

    @Override
    public int user2uidx(U u) {
        return ((FastPointWisePreferenceData<U, I>) d).user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return ((FastPointWisePreferenceData<U, I>) d).uidx2user(uidx);
    }

    @Override
    public int item2iidx(I i) {
        return ((FastPointWisePreferenceData<U, I>) d).item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return ((FastPointWisePreferenceData<U, I>) d).iidx2item(iidx);
    }

    @Override
    public IntIterator getUidxIidxs(int uidx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DoubleIterator getUidxVs(int uidx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntIterator getIidxUidxs(int iidx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DoubleIterator getIidxVs(int iidx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean useIteratorsPreferentially() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
