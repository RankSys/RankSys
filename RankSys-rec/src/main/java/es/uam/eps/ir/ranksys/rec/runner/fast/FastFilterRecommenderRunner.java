/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.runner.fast;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.Recommender;
import es.uam.eps.ir.ranksys.rec.fast.FastRecommender;
import es.uam.eps.ir.ranksys.rec.runner.AbstractRecommenderRunner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 * Fast filter runner. It creates recommendations by using the filter method in the
 * fast recommenders.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class FastFilterRecommenderRunner<U, I> extends AbstractRecommenderRunner<U, I> {

    private final FastUserIndex<U> userIndex;
    private final FastItemIndex<I> itemIndex;
    private final Function<U, IntPredicate> userFilter;
    private final int maxLength;

    /**
     * Constructor.
     *
     * @param userIndex fast user index
     * @param itemIndex fast item index
     * @param users target users
     * @param userFilter item filter provider for each user
     * @param maxLength maximum length of the recommendation lists, 0 for no limit
     */
    public FastFilterRecommenderRunner(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex, Stream<U> users, Function<U, IntPredicate> userFilter, int maxLength) {
        super(users);
        this.userIndex = userIndex;
        this.itemIndex = itemIndex;
        this.userFilter = userFilter;
        this.maxLength = maxLength;
    }

    @Override
    public void run(Recommender<U, I> recommender, Consumer<Recommendation<U, I>> consumer) {
        run(user -> {
            FastRecommendation rec = ((FastRecommender<U, I>) recommender).getRecommendation(userIndex.user2uidx(user), maxLength, userFilter.apply(user));
            
            return new Recommendation<>(userIndex.uidx2user(rec.getUidx()), rec.getIidxs().stream()
                    .map(itemIndex::iidx2item)
                    .collect(toList()));
        }, consumer);
    }

}
