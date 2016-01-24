/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import java.util.Optional;

/**
 * Point-wise preference data.
 * 
 * In some cases it is necessary to access a particular user-item pair.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface PointWisePreferenceData<U, I> extends PreferenceData<U, I> {
    
    /**
     * Get preference of a user for an item.
     *
     * @param u user
     * @param i item
     * @return optional preference for item if it exists
     */
    public Optional<? extends IdPref<I>> getPreference(U u, I i);
}
