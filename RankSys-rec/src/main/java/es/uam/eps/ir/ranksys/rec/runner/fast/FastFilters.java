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
package es.uam.eps.ir.ranksys.rec.runner.fast;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Function;
import java.util.function.IntPredicate;

/**
 * Filters for the filter recommender method in FastRecommender.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FastFilters {

    /**
     * False item filter as it admits every item.
     *
     * @return item filters that return true always
     */
    public static  <U, I> Function<U, IntPredicate> all() {
        return uidx -> iidx -> true;
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
    public static <U, I> Function<U, IntPredicate> notInTrain(FastPreferenceData<U, I> trainData) {
        return user -> {
            IntSet set = new IntOpenHashSet();
            trainData.getUidxPreferences(trainData.user2uidx(user)).mapToInt(iv -> iv.idx).forEach(set::add);

            return iidx -> !set.contains(iidx);
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
    public static <U, I, F> Function<U, IntPredicate> withFeatures(FastFeatureData<I, F, ?> featureData) {
        IntSet itemsWithFeatures = new IntOpenHashSet();
        featureData.getIidxWithFeatures().forEach(iidx -> itemsWithFeatures.add(iidx));
        return user -> iidx -> itemsWithFeatures.contains(iidx);
    }

    /**
     * AND of two or more filters.
     *
     * @param <U> type of the users
     * @param filters a number of item filters
     * @return an item filter which does a logical AND to two or more filters
     */
    @SuppressWarnings("unchecked")
    public static <U> Function<U, IntPredicate> and(Function<U, IntPredicate>... filters) {
        return user -> {
            IntPredicate andPredicate = iidx -> true;
            for (Function<U, IntPredicate> filter : filters) {
                andPredicate = andPredicate.and(filter.apply(user));
            }
            return andPredicate;
        };
    }
}
