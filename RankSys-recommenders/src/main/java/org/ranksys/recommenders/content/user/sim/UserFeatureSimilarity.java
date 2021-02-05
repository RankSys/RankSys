/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.user.sim;

import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.recommenders.content.sim.FeatureSimilarity;
import org.ranksys.recommenders.nn.user.sim.UserSimilarity;

/**
 * Item similarity. It wraps a generic fast feature similarity and a fast user index.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users
 */
public class UserFeatureSimilarity<U> extends UserSimilarity<U>
{

    /**
     * Constructor.
     *
     * @param uIndex fast user index
     * @param sim    generic fast similarity
     */
    public UserFeatureSimilarity(FastUserIndex<U> uIndex, FeatureSimilarity sim)
    {
        super(uIndex, sim);
    }
}
