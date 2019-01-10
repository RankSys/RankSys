/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index.fast.updateable;

import java.util.stream.Stream;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.index.updateable.UpdateableUserIndex;

/**
 * Fast and updateable version of UserIndex, where users are internally represented with numerical indices from 0 (inclusive) to the number of indexed users (exclusive).
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 */
public interface FastUpdateableUserIndex<U> extends UpdateableUserIndex<U>, FastUserIndex<U> 
{
    @Override
    public default Stream<U> getAllUsers() 
    {
        return getAllUidx().mapToObj(uidx -> uidx2user(uidx));
    }
}
