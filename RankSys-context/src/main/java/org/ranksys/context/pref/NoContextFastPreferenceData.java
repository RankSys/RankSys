/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.fast.preference.FastPointWisePreferenceData;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
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
    public IdxPrefCtx<C> getPreference(int uidx, int iidx) {
        IdxPref p = ((FastPointWisePreferenceData<U, I>) d).getPreference(uidx, iidx);
        if (p == null) {
            return null;
        } else {
            return new IdxPrefCtx<>(p.idx, p.v, Arrays.asList(emptyCtx));
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
