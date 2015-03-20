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

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class AbstractRecommender<U, I> implements Recommender<U, I> {

    protected PreferenceData<U, I, ?> data;

    public AbstractRecommender(PreferenceData<U, I, ?> data) {
        this.data = data;
    }

    @Override
    public Recommendation<U, I> getRecommendation(final U u, int maxLength) {
        return getRecommendation(u, maxLength, i -> true);
    }

    @Override
    public abstract Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter);

    @Override
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates) {
        Set<I> set = candidates.collect(Collectors.toSet());

        return getRecommendation(u, 0, item -> set.contains(item));
    }
}
