/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.feature.user.fast;

import org.ranksys.core.feature.user.UserFeatureData;
import org.ranksys.core.index.fast.FastFeatureIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.util.tuples.Tuple2io;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface FastUserFeatureData<U,F,V> extends UserFeatureData<U, F, V>, FastUserIndex<U>, FastFeatureIndex<F>
{
    /**
     * Returns the features associated with a user.
     *
     * @param uidx user index
     * @return features associated with the user
     */
    Stream<Tuple2io<V>> getUidxFeatures(final int uidx);

    /**
     * Returns the users having a feature.
     *
     * @param fidx feature index
     * @return users having the feature
     */
    Stream<Tuple2io<V>> getFidxUsers(final int fidx);

    /**
     * Returns the number of users having a feature.
     *
     * @param fidx feature index
     * @return number of users having the feature
     */
    int numUsers(int fidx);

    /**
     * Returns the number of features associated with a user.
     *
     * @param iidx item index
     * @return number of features associated with the item
     */
    int numFeatures(int iidx);

    /**
     * Returns the indexes of the users with features.
     *
     * @return a stream of indexes of users with features
     */
    IntStream getUidxWithFeatures();

    /**
     * Returns the features that are associated with users.
     *
     * @return a stream of indexes of features with users
     */
    IntStream getFidxWithUsers();
}
