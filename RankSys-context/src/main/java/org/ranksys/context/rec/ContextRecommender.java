/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rec;

import es.uam.eps.ir.ranksys.core.Recommendation;
import org.ranksys.context.ContextRecommendation;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import static org.jooq.lambda.tuple.Tuple.tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Context-aware recommender. Generates a list of recommended items for a user in a given context.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 *
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public interface ContextRecommender<U, I, C> extends Recommender<Tuple2<U, C>, I> {

    public default Recommendation<Tuple2<U, C>, I> getRecommendation(U u, C c) {
        List<Tuple2od<I>> items = getRecommendation(tuple(u, c)).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, int maxLength) {
        List<Tuple2od<I>> items = getRecommendation(tuple(u, c), maxLength).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, Predicate<I> filter) {
        List<Tuple2od<I>> items = getRecommendation(tuple(u, c), filter).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, int maxLength, Predicate<I> filter) {
        List<Tuple2od<I>> items = getRecommendation(tuple(u, c), maxLength, filter).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, Stream<I> candidates) {
        List<Tuple2od<I>> items = getRecommendation(tuple(u, c), candidates).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

}
