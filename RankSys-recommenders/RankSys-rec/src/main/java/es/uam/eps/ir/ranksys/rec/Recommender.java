/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
     * on the items being recommended, but with a limit on the list size.
     *
     * @param u user to be issued a recommendation
     * @param maxLength maximum length of recommendation, set to 0 for no limit
     * @return a recommendation list
     */
    public Recommendation<U, I> getRecommendation(U u, int maxLength);

    /**
     * Filter recommendation. Recommends only the items that pass the filter up
     * to a maximum list size.
     *
     * @param u user to be issued a recommendation
     * @param maxLength maximum length of recommendation, set to 0 for no limit
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
