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
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new AvgUserMMR(recommendation);
    }

    public class AvgUserMMR extends LambdaUserReranker {

        private final TObjectDoubleMap<I> avgDist;
        private int n;

        public AvgUserMMR(Recommendation<U, I> recommendation) {
            super(recommendation);

            n = 0;
            avgDist = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);
            recommendation.getItems().stream().sequential()
                    .map(iv -> iv.id)
                    .forEach(i -> avgDist.put(i, 0.0));
        }

        @Override
        protected double nov(IdDoublePair<I> itemValue) {
            return avgDist.get(itemValue.id);
        }

        @Override
        protected void update(IdDoublePair<I> bestItemValue) {
            I bestItem = bestItemValue.id;
            avgDist.remove(bestItem);

            n++;
            avgDist.transformValues(d -> d * ((n - 1) / (double) n));
            avgDist.forEachEntry((i, d) -> {
                double d2 = dist.dist(i, bestItem) / n;
                avgDist.adjustOrPutValue(i, d2, d2);
                return true;
            });
        }

    }
}
