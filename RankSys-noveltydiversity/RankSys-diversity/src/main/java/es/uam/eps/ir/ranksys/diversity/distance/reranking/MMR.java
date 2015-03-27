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
package es.uam.eps.ir.ranksys.diversity.distance.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.LambdaReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.function.ToDoubleFunction;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class MMR<U, I> extends LambdaReranker<U, I> {

    private final ItemDistanceModel<I> dist;

    public MMR(double lambda, int cutoff, ItemDistanceModel<I> dist) {
        super(lambda, cutoff, true);

        this.dist = dist;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new UserMMR(recommendation, maxLength);
    }

    public class UserMMR extends LambdaUserReranker {

        private final Object2DoubleOpenHashMap<I> avgDist;
        private int n;

        public UserMMR(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            n = 0;
            avgDist = new Object2DoubleOpenHashMap<>();
            avgDist.defaultReturnValue(0.0);
            recommendation.getItems().stream().sequential()
                    .map(iv -> iv.id)
                    .forEach(i -> avgDist.put(i, 0.0));
        }

        @Override
        protected double nov(IdDouble<I> itemValue) {
            return avgDist.getDouble(itemValue.id);
        }

        @Override
        protected void update(IdDouble<I> bestItemValue) {
            I bestItem = bestItemValue.id;
            ToDoubleFunction<I> bDist = dist.dist(bestItem);
            avgDist.remove(bestItem);

            n++;
            avgDist.object2DoubleEntrySet().forEach(e -> {
                I i = e.getKey();
                double d = e.getDoubleValue();
                double d2 = bDist.applyAsDouble(i);
                avgDist.addTo(i, (d2 - d) / n);
            });
        }

    }
}
