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

import org.ranksys.context.ContextRecommendation;
import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public interface ContextRecommender<U, I, C> extends Recommender<IdObject<U, C>, I> {

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, int maxLength) {
        List<IdDouble<I>> items = getRecommendation(new IdObject<>(u, c), maxLength).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, int maxLength, Predicate<I> filter) {
        List<IdDouble<I>> items = getRecommendation(new IdObject<>(u, c), maxLength, filter).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

    public default ContextRecommendation<U, I, C> getRecommendation(U u, C c, Stream<I> candidates) {
        List<IdDouble<I>> items = getRecommendation(new IdObject<>(u, c), candidates).getItems();
        return new ContextRecommendation<>(u, c, items);
    }

}
