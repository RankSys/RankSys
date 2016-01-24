/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
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
package org.ranksys.context.rec;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class NoContextRecommender<U, I, C> implements ContextRecommender<U, I, C> {

    private final Recommender<U, I> recommender;

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
