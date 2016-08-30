/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import org.ranksys.core.preference.PreferenceSampler;
import org.ranksys.fast.index.FastItemIndex;
import org.ranksys.fast.index.FastUserIndex;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2io;

/**
 * Fast preference sampler, for stochastic algorithms.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * @param <U> user type
 * @param <I> item type
 */
public interface FastPreferenceSampler<U, I> extends PreferenceSampler<U, I>, FastUserIndex<U>, FastItemIndex<I> {

    /**
     * Get a stream of randomly sample user-item fast preferences.
     *
     * @return stream of user-item fast preferences
     */
    public Stream<Tuple2io<? extends IdxPref>> fastSample();

}
