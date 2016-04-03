/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rec;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Wrapper for a regular recommender as a context-aware one.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 *
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public class NoContextRecommender<U, I, C> implements ContextRecommender<U, I, C> {

    private final Recommender<U, I> recommender;

    /**
     * Constructor.
     *
     * @param recommender wrapped recommender
     */
    public NoContextRecommender(Recommender<U, I> recommender) {
        this.recommender = recommender;
    }

    @Override
    public Recommendation<Tuple2<U, C>, I> getRecommendation(Tuple2<U, C> u) {
        List<Tuple2od<I>> items = recommender.getRecommendation(u.v1).getItems();
        return new Recommendation<>(u, items);
    }

    @Override
    public Recommendation<Tuple2<U, C>, I> getRecommendation(Tuple2<U, C> u, int maxLength) {
        List<Tuple2od<I>> items = recommender.getRecommendation(u.v1, maxLength).getItems();
        return new Recommendation<>(u, items);
    }

    @Override
    public Recommendation<Tuple2<U, C>, I> getRecommendation(Tuple2<U, C> u, Predicate<I> filter) {
        List<Tuple2od<I>> items = recommender.getRecommendation(u.v1, filter).getItems();
        return new Recommendation<>(u, items);
    }

    @Override
    public Recommendation<Tuple2<U, C>, I> getRecommendation(Tuple2<U, C> u, int maxLength, Predicate<I> filter) {
        List<Tuple2od<I>> items = recommender.getRecommendation(u.v1, maxLength, filter).getItems();
        return new Recommendation<>(u, items);
    }

    @Override
    public Recommendation<Tuple2<U, C>, I> getRecommendation(Tuple2<U, C> u, Stream<I> candidates) {
        List<Tuple2od<I>> items = recommender.getRecommendation(u.v1, candidates).getItems();
        return new Recommendation<>(u, items);
    }
}
