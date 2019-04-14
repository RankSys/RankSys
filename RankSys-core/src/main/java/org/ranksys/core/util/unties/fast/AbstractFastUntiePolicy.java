/* 
 * Copyright (C) 2019 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.unties.fast;

import org.ranksys.core.index.fast.FastItemIndex;

/**
 * Abstract implementation of the fast untie policy class.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <T> type of the elements to compare.
 */
public abstract class AbstractFastUntiePolicy<T> implements FastUntiePolicy
{
    /**
     * An index containing the identifiers of the elements to untie.
     */
    protected final FastItemIndex<T> index;
    
    /**
     * Constructor.
     * @param index the index. 
     */
    public AbstractFastUntiePolicy(FastItemIndex<T> index)
    {
        this.index = index;
    }
    
}
