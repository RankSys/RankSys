/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.fast.basic;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import java.util.List;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import static java.util.Collections.sort;
import org.ranksys.core.util.tuples.Tuple2id;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Popularity-based recommender. Non-personalized recommender that returns the
 * most popular items according to the preference data provided.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class PopularityRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final List<Tuple2id> popList;

    /**
     * Constructor.
     *
     * @param data preference data
     */
    public PopularityRecommender(FastPreferenceData<U, I> data) {
        super(data, data);

        popList = data.getIidxWithPreferences()
                .mapToObj(iidx -> tuple(iidx, (double) data.numUsers(iidx)))
                .collect(toList());
        sort(popList, (p1, p2) -> Double.compare(p2.v2, p1.v2));
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = popList.size();
        }
        
        List<Tuple2id> items = popList.stream()
                .filter(is -> filter.test(is.v1))
                .limit(maxLength)
                .collect(toList());
        
        return new FastRecommendation(uidx, items);
    }
}
