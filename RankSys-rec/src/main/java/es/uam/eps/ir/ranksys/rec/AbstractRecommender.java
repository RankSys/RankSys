/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract recommender. It implements the free and candidate-based 
 * recommendation methods as variants of the filter recommendation.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class AbstractRecommender<U, I> implements Recommender<U, I> {

    @Override
    public Recommendation<U, I> getRecommendation(final U u, int maxLength) {
        return getRecommendation(u, maxLength, i -> true);
    }

    @Override
    public abstract Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter);

    @Override
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates) {
        Set<I> set = candidates.collect(Collectors.toCollection(() -> new HashSet<>()));
        List<IdDouble<I>> items = getRecommendation(u, 0, item -> set.contains(item)).getItems();
        items.forEach(is -> set.remove(is.id));
        set.stream().sorted().forEach(i -> items.add(new IdDouble<>(i, Double.NaN)));
        
        return new Recommendation<>(u, items);
    }
}
