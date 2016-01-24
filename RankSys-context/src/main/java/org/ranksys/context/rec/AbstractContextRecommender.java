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
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.rec.AbstractRecommender;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public abstract class AbstractContextRecommender<U, I, C> extends AbstractRecommender<IdObject<U, C>, I> implements ContextRecommender<U, I, C> {

    @Override
    public ContextRecommendation<U, I, C> getRecommendation(final U u, final C c, int maxLength) {
        return getRecommendation(u, c, maxLength, i -> true);
    }

    @Override
    public ContextRecommendation<U, I, C> getRecommendation(final U u, final C c, Stream<I> candidates) {
        Set<I> set = candidates.collect(Collectors.toSet());

        return getRecommendation(u, c, 0, item -> set.contains(item));
    }

}
