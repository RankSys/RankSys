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
package es.uam.eps.ir.ranksys.diversity.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import static java.lang.Double.isNaN;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public abstract class GreedyReranker<U, I> extends PermutationReranker<U, I> {

    protected final int cutoff;

    public GreedyReranker() {
        this.cutoff = Integer.MAX_VALUE;
    }

    public GreedyReranker(int cutoff) {
        this.cutoff = cutoff;
    }

    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation) {
        return getUserReranker(recommendation).rerankPermutation();
    }

    protected abstract GreedyUserReranker<U, I> getUserReranker(Recommendation<U, I> recommendation);

    protected abstract class GreedyUserReranker<U, I> {

        protected final Recommendation<U, I> recommendation;

        public GreedyUserReranker(Recommendation<U, I> recommendation) {
            this.recommendation = recommendation;
        }

        public int[] rerankPermutation() {

            U user = recommendation.getUser();
            List<IdDoublePair<I>> list = recommendation.getItems();

            int[] perm = new int[min(cutoff, list.size())];
            List<IdDoublePair<I>> remaining = new ArrayList<>(list);
            List<IdDoublePair<I>> reranked = new ArrayList<>();

            while (!remaining.isEmpty() && reranked.size() < cutoff) {
                IdDoublePair<I> bestItem = selectItem(user, remaining, reranked);

                perm[reranked.size()] = list.indexOf(bestItem);
                reranked.add(bestItem);
                remaining.remove(bestItem);

                update(user, bestItem);
            }
            reranked.addAll(remaining);

            return perm;
        }

        protected IdDoublePair<I> selectItem(U user, List<IdDoublePair<I>> remaining, List<IdDoublePair<I>> reranked) {
            double max = Double.NEGATIVE_INFINITY;
            IdDoublePair<I> bestItemValue = remaining.get(0);
            for (IdDoublePair<I> itemValue : remaining) {
                double value = value(user, itemValue, reranked);
                if (isNaN(value)) {
                    continue;
                }
                if (value > max) {
                    max = value;
                    bestItemValue = itemValue;
                }
            }
            return bestItemValue;
        }

        protected abstract double value(U user, IdDoublePair<I> itemValue, List<IdDoublePair<I>> reranked);

        protected abstract void update(U user, IdDoublePair<I> bestItemValue);
    }

}
