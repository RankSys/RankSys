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
import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.index.updateable.UpdateableItemIndex;

/**
 * Fast updateable version of ItemIndex, where items are internally represented with numerical indices from 0 (inclusive) to the number of indexed items (exclusive).
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the items
 */
public interface FastUpdateableItemIndex<I> extends UpdateableItemIndex<I>, FastItemIndex<I> 
{
    @Override
    public default Stream<I> getAllItems() 
    {
        return getAllIidx().mapToObj(iidx -> iidx2item(iidx));
    }
}
