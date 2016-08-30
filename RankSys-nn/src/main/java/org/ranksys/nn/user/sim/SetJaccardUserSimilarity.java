/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.nn.user.sim;

import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.nn.sim.SetJaccardSimilarity;

/**
 * Set Jaccard user similarity. See {@link SetJaccardSimilarity}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public class SetJaccardUserSimilarity<U> extends UserSimilarity<U> {

    /**
     * Constructor.
     *
     * @param data preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public SetJaccardUserSimilarity(FastPreferenceData<U, ?> data, boolean dense) {
        super(data, new SetJaccardSimilarity(data, dense));
    }

}
