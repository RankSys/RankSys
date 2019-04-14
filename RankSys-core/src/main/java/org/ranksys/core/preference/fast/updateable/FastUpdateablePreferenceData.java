/*
 *  Copyright (C) 2016 Information Retrieval Group at Universidad Aut�noma
 *  de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.preference.fast.updateable;

import org.ranksys.core.index.fast.updateable.FastUpdateableItemIndex;
import org.ranksys.core.index.fast.updateable.FastUpdateableUserIndex;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.preference.updateable.UpdateablePreferenceData;

/**
 * Interface for updateable preference data.
 * @author Javier Sanz-Cruzado Puig
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public interface FastUpdateablePreferenceData<U,I> extends UpdateablePreferenceData<U,I>, FastPreferenceData<U,I>, FastUpdateableUserIndex<U>, FastUpdateableItemIndex<I>
{
    
}
