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
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Filters for the filter recommender method in Recommender.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Filters {

    /**
     * False item filter as it admits every item.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @return item filters that return true always
     */
    public static <U, I> Function<U, Predicate<I>> all() {
        return user -> item -> true;
    }

    /**
     * Item filter that discards items in the training preference data.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param trainData preference data
     * @return item filters for each using returning true if the
     * user-item pair was not observed in the preference data
     */
    public static <U, I> Function<U, Predicate<I>> notInTrain(PreferenceData<U, I, ?> trainData) {
        return user -> {
            Set<I> set = trainData.getUserPreferences(user).map(iv -> iv.id).collect(Collectors.toSet());

            return i -> !set.contains(i);
        };
    }

    /**
     * Item filter that discard items for which no feature data is available.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param <F> type of the features
     * @param featureData feature data
     * @return item filters that return true when there is any feature
     * information for the item
     */
    public static <U, I, F> Function<U, Predicate<I>> withFeatures(FeatureData<I, F, ?> featureData) {
        Set<I> itemsWithFeatures = featureData.getItemsWithFeatures().collect(Collectors.toSet());
        return user -> item -> itemsWithFeatures.contains(item);
    }

    /**
     * AND of two or more filters.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param filters a number of item filters
     * @return an item filter which does a logical AND to two or more filters
     */
    @SuppressWarnings("unchecked")
    public static <U, I> Function<U, Predicate<I>> and(Function<U, Predicate<I>>... filters) {
        return user -> {
            Predicate<I> andPredicate = item -> true;
            for (Function<U, Predicate<I>> filter : filters) {
                andPredicate = andPredicate.and(filter.apply(user));
            }
            return andPredicate;
        };
    }
}
