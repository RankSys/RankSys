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
package es.uam.eps.ir.ranksys.rec.runner.fast;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.Recommender;
import es.uam.eps.ir.ranksys.rec.fast.FastRecommender;
import es.uam.eps.ir.ranksys.rec.runner.AbstractRecommendationRunner;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FastFilterRecommendationRunner<U, I> extends AbstractRecommendationRunner<U, I> {

    private final FastUserIndex<U> userIndex;
    private final FastItemIndex<I> itemIndex;
    private final Function<U, IntPredicate> userFilter;
    private final int maxLength;

    public FastFilterRecommendationRunner(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex, Set<U> users, RecommendationFormat<U, I> format, Function<U, IntPredicate> userFilter, int maxLength) {
        super(users.stream(), format);
        this.userIndex = userIndex;
        this.itemIndex = itemIndex;
        this.userFilter = userFilter;
        this.maxLength = maxLength;
    }

    @Override
    public void run(Recommender<U, I> recommender, OutputStream out) throws IOException {
        run(user -> {
            FastRecommendation<U, I> rec = ((FastRecommender<U, I>) recommender).getRecommendation(userIndex.user2uidx(user), maxLength, userFilter.apply(user));
            
            return new Recommendation<>(userIndex.uidx2user(rec.getUidx()), rec.getIidxs().stream().map(iv -> new IdDouble<I>(itemIndex.iidx2item(iv.idx), iv.v)).collect(Collectors.toList()));
        }, out);
    }

}
