/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.fast;

import es.uam.eps.ir.ranksys.rec.Recommender;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

/**
 * Fast recommender. A version that uses the functionalities of RankSys-fast,
 * so it works only with user and item integer indexes.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface FastRecommender<U, I> extends Recommender<U, I>, FastUserIndex<U>, FastItemIndex<I> {

    /**
     * Free recommendation. Generate recommendations without any restriction
     * on the items being recommended.
     *
     * @param uidx index of the user to be issued a recommendation
     * @return a (fast) recommendation list
     */
    public FastRecommendation getRecommendation(int uidx);

    /**
     * Free recommendation. Generate recommendations without any restriction
     * on the items being recommended, but with a limit on the list size.
     *
     * @param uidx index of the user to be issued a recommendation
     * @param maxLength maximum length of recommendation
     * @return a (fast) recommendation list
     */
    public FastRecommendation getRecommendation(int uidx, int maxLength);

    /**
     * Filter recommendation. Recommends only the items that pass the filter.
     *
     * @param uidx index of the user to be issued a recommendation
     * @param filter (fast) filter to decide which items might be recommended
     * @return a (fast) recommendation list
     */
    public FastRecommendation getRecommendation(int uidx, IntPredicate filter);

    /**
     * Filter recommendation. Recommends only the items that pass the filter up
     * to a maximum list size.
     *
     * @param uidx index of the user to be issued a recommendation
     * @param maxLength maximum length of recommendation
     * @param filter (fast) filter to decide which items might be recommended
     * @return a (fast) recommendation list
     */
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter);

    /**
     * Candidates ranking. Create a list that may contain only the items
     * in the candidates set.
     *
     * @param uidx item of the user to be issued a recommendation
     * @param candidates candidate items to be included in the recommendation
     * @return a recommendation list
     */
    public FastRecommendation getRecommendation(int uidx, IntStream candidates);

}
