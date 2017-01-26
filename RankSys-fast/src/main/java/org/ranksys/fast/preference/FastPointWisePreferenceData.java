/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import org.ranksys.core.preference.PointWisePreferenceData;

import java.util.Optional;

/**
 * Fast point-wise preference data.
 *
 * In some cases it is necessary to access a particular user-item pair.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface FastPointWisePreferenceData<U, I> extends PointWisePreferenceData<U, I>, FastPreferenceData<U, I> {

    /**
     * Get preference of a user for an item.
     *
     * @param uidx user idx
     * @param iidx item idx
     * @return optional preference for item if it exists
     */
    Optional<? extends IdxPref> getPreference(int uidx, int iidx);
}
