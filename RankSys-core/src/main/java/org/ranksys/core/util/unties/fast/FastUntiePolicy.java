/* 
 * Copyright (C) 2019 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.unties.fast;

import java.util.Comparator;
import org.ranksys.core.util.unties.UntiePolicy;

/**
 * Class for deciding which key is better in case of tie.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <T> the object to compare.
 */
public interface FastUntiePolicy<T> extends UntiePolicy<T>
{
    /**
     * Obtains a comparator
     * @return a comparator.
     */
    public Comparator<Integer> fastComparator();
}
