/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.preference.IdPref;
import org.ranksys.core.preference.PreferenceSampler;
import es.uam.eps.ir.ranksys.fast.IdxObject;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import java.util.stream.Stream;

/**
 * Fast preference sampler, for stochastic algorithms.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * @param <U> user type
 * @param <I> item type
 */
public interface FastPreferenceSampler<U, I> extends PreferenceSampler<U, I>, FastUserIndex<U>, FastItemIndex<I> {

    @Override
    public default Stream<IdObject<U, ? extends IdPref<I>>> sample() {
        return fastSample().map(pref -> {
            U u = uidx2user(pref.idx);
            I i = iidx2item(pref.v.idx);
            double v = pref.v.v;
            return new IdObject<U, IdPref<I>>(u, new IdPref<>(i, v));
        });
    }

    /**
     *
     * @return
     */
    public Stream<IdxObject<? extends IdxPref>> fastSample();

}
