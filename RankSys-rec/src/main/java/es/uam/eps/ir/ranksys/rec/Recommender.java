/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec;

import es.uam.eps.ir.ranksys.core.Recommendation;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Recommender interface. All static recommendation algorithms implement
 * this interface.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface Recommender<U, I> {

    /**
     * Free recommendation. Generate recommendations without any restriction
     * on the items being recommended.
     *
     * @param u user to be issued a recommendation
     * @return a recommendation list
     */
    public Recommendation<U, I> getRecommendation(U u);

    /**
     * Free recommendation. Generate recommendations without any restriction
     * on the items being recommended, but with a limit on the list size.
     *
     * @param u user to be issued a recommendation
     * @param maxLength maximum length of recommendation
     * @return a recommendation list
     */
    public Recommendation<U, I> getRecommendation(U u, int maxLength);

    /**
     * Filter recommendation. Recommends only the items that pass the filter.
     *
     * @param u user to be issued a recommendation
     * @param filter filter to decide which items might be recommended
     * @return a recommendation list
     */
    public Recommendation<U, I> getRecommendation(U u, Predicate<I> filter);

    /**
     * Filter recommendation. Recommends only the items that pass the filter up
     * to a maximum list size.
     *
     * @param u user to be issued a recommendation
     * @param maxLength maximum length of recommendation
     * @param filter filter to decide which items might be recommended
     * @return a recommendation list
     */
    public Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter);

    /**
     * Candidates ranking. Create a list that may contain only the items
     * in the candidates set.
     *
     * @param u user to be issued a recommendation
     * @param candidates candidate items to be included in the recommendation
     * @return a recommendation list
     */
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates);

}
