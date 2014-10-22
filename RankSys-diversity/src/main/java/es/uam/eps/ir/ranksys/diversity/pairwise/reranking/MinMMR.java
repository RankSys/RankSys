/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.diversity.pairwise.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.pairwise.ItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class MinMMR<U, I> extends LambdaReranker<U, I> {

    private final ItemDistanceModel<I> dist;

    public MinMMR(double lambda, int cutoff, ItemDistanceModel<I> dist) {
        super(lambda, cutoff, true);

        this.dist = dist;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new MinUserMMR(recommendation);
    }

    public class MinUserMMR extends LambdaUserReranker {

        private final TObjectDoubleMap<I> minDist;

        public MinUserMMR(Recommendation<U, I> recommendation) {
            super(recommendation);

            minDist = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);
            recommendation.getItems().stream().sequential()
                    .map(iv -> iv.id)
                    .forEach(i -> minDist.put(i, Double.POSITIVE_INFINITY));
        }

        @Override
        protected double nov(U user, IdDoublePair<I> itemValue, List<IdDoublePair<I>> reranked) {
            return minDist.get(itemValue.id);
        }

        @Override
        protected void update(U user, IdDoublePair<I> bestItemValue) {
            I bestItem = bestItemValue.id;
            minDist.remove(bestItem);

            minDist.forEachEntry((i, d) -> {
                minDist.put(i, Math.min(d, dist.dist(i, bestItem)));
                return true;
            });
        }

    }
}
