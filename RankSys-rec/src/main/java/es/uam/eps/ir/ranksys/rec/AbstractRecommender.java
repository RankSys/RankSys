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
