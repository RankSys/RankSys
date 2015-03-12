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
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FilterRecommendationRunner<U, I> extends AbstractRecommendationRunner<U, I> {

    private final Function<U, Predicate<I>> userFilter;
    private final int maxLength;

    public FilterRecommendationRunner(Set<U> users, RecommendationFormat<U, I> format, Function<U, Predicate<I>> userFilter, int maxLength) {
        super(users.stream(), format);
        
        this.userFilter = userFilter;
        this.maxLength = maxLength;
    }

    @Override
    public void run(final Recommender<U, I> recommender, OutputStream out) throws IOException {
        run(user -> recommender.getRecommendation(user, maxLength, userFilter.apply(user)), out);
    }
}
