/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.pref;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.ranksys.context.pref.ContextFastPreferenceData.IdxPrefCtx;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleContextFastPreferenceData<U, I, C, P extends IdxPrefCtx<C>> extends SimpleFastPreferenceData<U, I, P> implements ContextFastPreferenceData<U, I, C> {

    private final int contextSize;

    public SimpleContextFastPreferenceData(int numPreferences, List<List<P>> uidxList, List<List<P>> iidxList, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, int contextSize) {
        this(numPreferences, uidxList, iidxList, uIndex, iIndex,
                (Function<P, IdPref<I>> & Serializable) p -> new IdPrefCtx<>(iIndex.iidx2item(p.v1), p.v2, p.cs),
                (Function<P, IdPref<U>> & Serializable) p -> new IdPrefCtx<>(uIndex.uidx2user(p.v1), p.v2, p.cs),
                contextSize);
    }

    public SimpleContextFastPreferenceData(int numPreferences, List<List<P>> uidxList, List<List<P>> iidxList, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, Function<P, IdPref<I>> uPrefFun, Function<P, IdPref<U>> iPrefFun, int contextSize) {
        super(numPreferences, uidxList, iidxList, uIndex, iIndex, uPrefFun, iPrefFun);
        this.contextSize = contextSize;
    }

    @Override
    public Stream<? extends IdPrefCtx<I, C>> getUserPreferences(U u) {
        return super.getUserPreferences(u).map(p -> (IdPrefCtx<I, C>) p);
    }

    @Override
    public Stream<? extends IdPrefCtx<U, C>> getItemPreferences(I i) {
        return super.getItemPreferences(i).map(p -> (IdPrefCtx<U, C>) p);
    }

    @Override
    public Optional<? extends IdPrefCtx<I, C>> getPreference(U u, I i) {
        return super.getPreference(u, i).map(p -> (IdPrefCtx<I, C>) p);
    }

    @Override
    public int getContextSize() {
        return contextSize;
    }

}
