/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Filter runner. It creates recommendations by using the filter method in the
 * recommenders.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class FilterRecommenderRunner<U, I> extends AbstractRecommenderRunner<U, I> {

    private final Function<U, Predicate<I>> userFilter;
    private final int maxLength;

    /**
     * Constructor.
     *
     * @param users target users, those for which recommendations are generated.
     * @param format output recommendation format
     * @param userFilter item filter provider for each user
     * @param maxLength maximum length of the recommendation lists, 0 for no limit
     */
    public FilterRecommenderRunner(Set<U> users, RecommendationFormat<U, I> format, Function<U, Predicate<I>> userFilter, int maxLength) {
        super(users.stream(), format);
        
        this.userFilter = userFilter;
        this.maxLength = maxLength;
    }

    @Override
    public void run(final Recommender<U, I> recommender, OutputStream out) throws IOException {
        run(user -> recommender.getRecommendation(user, maxLength, userFilter.apply(user)), out);
    }
}
