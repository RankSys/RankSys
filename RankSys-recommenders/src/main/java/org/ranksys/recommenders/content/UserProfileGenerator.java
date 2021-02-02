/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content;

import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.core.feature.user.fast.FastUserFeatureData;
import org.ranksys.core.preference.fast.FastPreferenceData;

@FunctionalInterface
public interface UserProfileGenerator<U,I,F,V>
{
    /**
     * Interface.
     * @param prefData preference data.
     * @param itemFeatureData item feature data.
     * @return a feature data for the users.
     */
    FastUserFeatureData<U,F,V> create(FastPreferenceData<U,I> prefData, FastItemFeatureData<I,F,V> itemFeatureData);
}
