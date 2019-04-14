/* 
 * Copyright (C) 2019 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.utils.filler;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.ranksys.core.Recommendation;

/**
 * Class for personalizing how lists are filled.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public interface Filler<U,I>
{
    /**
     * Obtains the filler list for a given user.
     * @param u the user.
     * @return a stream containing the filler list for the user.
     */
    public Stream<I> fillerList(U u);
    
    /**
     * Given a recommendation, fills it with until it reaches the desired number of items (if possible).
     * If the cutoff is greater than the indicated one, the recommendation is trimmed.
     * @param rec the recommendation.
     * @param cutoff the cutoff.
     * @param pred a predicate for filtering the possible recommendations.
     * @return the new recommendation if everything is OK, null otherwise.
     */
    public Recommendation<U,I> fill(Recommendation<U,I> rec, int cutoff, Function<U, Predicate<I>> pred);
    
    /**
     * Number of elements filled.
     * @return the number of elements filled in the last recommendation.
     */
    public int numFilled();
    /**
     * Number of total elements in the recommendation.
     * @return the number of total elements in the recommendation.
     */
    public int numTotal();
    /**
     * Resets the filler.
     */
    public void reset();
}
