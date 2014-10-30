/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.diversity.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import static java.lang.Double.isNaN;
import static java.lang.Math.min;
import java.util.List;
import java.util.stream.IntStream;

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

            List<IdDoublePair<I>> list = recommendation.getItems();

            int[] perm = new int[min(cutoff, list.size())];
            TIntList remainingI = new TIntLinkedList();
            IntStream.range(0, list.size()).forEach(i -> remainingI.add(i));
            int nreranked = 0;

            while (!remainingI.isEmpty() && nreranked < cutoff) {
                int bestI = selectItem(remainingI, list);

                perm[nreranked] = bestI;
                nreranked++;
                remainingI.remove(bestI);

                update(list.get(bestI));
            }

            return perm;
        }

        protected int selectItem(TIntList remainingI, List<IdDoublePair<I>> list) {
            double[] max = new double[]{Double.NEGATIVE_INFINITY};
            int[] bestI = new int[]{remainingI.get(0)};
            remainingI.forEach(i -> {
                double value = value(list.get(i));
                if (isNaN(value)) {
                    return true;
                }
                if (value > max[0] || (value == max[0] && i < bestI[0])) {
                    max[0] = value;
                    bestI[0] = i;
                }
                return true;
            });

            return bestI[0];
        }

        protected abstract double value(IdDoublePair<I> itemValue);

        protected abstract void update(IdDoublePair<I> bestItemValue);
    }

}
