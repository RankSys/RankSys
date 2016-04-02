/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.preference;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import es.uam.eps.ir.ranksys.core.index.UserIndex;
import es.uam.eps.ir.ranksys.core.preference.IdPref;
import java.util.stream.Stream;

/**
 * Preference sampler, for stochastic algorithms.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * @param <U> user type
 * @param <I> item type
 */
public interface PreferenceSampler<U, I> extends UserIndex<U>, ItemIndex<I>{
    
    /**
     * Get a stream of randomly sample user-item preferences.
     *
     * @return stream of user-item preferences
     */
    public Stream<IdObject<U, ? extends IdPref<I>>> sample();
}
