/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rec;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
    public Recommendation<IdObject<U, C>, I> getRecommendation(IdObject<U, C> u, int maxLength) {
        List<IdDouble<I>> items = recommender.getRecommendation(u.id, maxLength).getItems();
        return new Recommendation<>(u, items);
    }

    @Override
    public Recommendation<IdObject<U, C>, I> getRecommendation(IdObject<U, C> u, int maxLength, Predicate<I> filter) {
        List<IdDouble<I>> items = recommender.getRecommendation(u.id, maxLength, filter).getItems();
        return new Recommendation<>(u, items);
    }

    @Override
    public Recommendation<IdObject<U, C>, I> getRecommendation(IdObject<U, C> u, Stream<I> candidates) {
        List<IdDouble<I>> items = recommender.getRecommendation(u.id, candidates).getItems();
        return new Recommendation<>(u, items);
    }
}
