/* 
 * Copyright (C) 2019 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.utils.filler;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.ranksys.core.fast.FastRecommendation;

/**
 * Fast version of the filler class.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public interface FastFiller<U,I> extends Filler<U,I>
{
    /**
     * Returns a list with the identifiers of the items.
     * @param uidx the identifier of the user.
     * @return a stream with the identifiers of the items used to fill lists for the user uidx
     */
    public IntStream fillerList(int uidx);
    
    /**
     * Given a recommendation, fills it with until it reaches the desired number of items (if possible).
     * If the cutoff is greater than the indicated one, the recommendation is trimmed. Uses the index
     * values of items.
     * @param rec the recommendation.
     * @param cutoff the cutoff.
     * @param pred a predicate for filtering the possible recommendations.
     * @return the new recommendation if everything is OK, null otherwise.
     */
    public FastRecommendation fastFill(FastRecommendation rec, int cutoff, Function<U, IntPredicate> pred);


}
