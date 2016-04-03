/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import org.ranksys.core.preference.PreferenceSampler;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2io;

/**
 * Fast preference sampler, for stochastic algorithms.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * @param <U> user type
 * @param <I> item type
 */
public interface FastPreferenceSampler<U, I> extends PreferenceSampler<U, I>, FastUserIndex<U>, FastItemIndex<I> {

    @Override
    public default Stream<Tuple2<U, ? extends IdPref<I>>> sample() {
        return fastSample().map(pref -> {
            U u = uidx2user(pref.v1);
            I i = iidx2item(pref.v2.v1);
            double v = pref.v2.v2;
            return Tuple.tuple(u, new IdPref<>(i, v));
        });
    }

    /**
     * Get a stream of randomly sample user-item fast preferences.
     *
     * @return stream of user-item fast preferences
     */
    public Stream<Tuple2io<? extends IdxPref>> fastSample();

}
