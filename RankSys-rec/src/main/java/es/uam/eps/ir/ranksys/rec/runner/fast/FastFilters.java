/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.runner.fast;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
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
     * @param <U> type of the users
     * @param <I> type of the items
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
            trainData.getUidxPreferences(trainData.user2uidx(user))
                    .mapToInt(IdxPref::v1)
                    .forEach(set::add);

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
     * For social network recommendations, void a user being recommended to herself.
     *
     * @param <U> type of the user
     * @param users  user index
     * @return item-user filter that return true if the recommended item-user is not the target user.
     */
    public static <U> Function<U, IntPredicate> notSelf(FastUserIndex<U> users) {
        return user1 -> uidx2 -> uidx2 != users.user2uidx(user1);
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
