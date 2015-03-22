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
package es.uam.eps.ir.ranksys.novdiv.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
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

    protected final int cutoff1;
    protected final int cutoff2;

    public GreedyReranker(int cutoff1, int cutoff2) {
        this.cutoff1 = cutoff1;
        this.cutoff2 = cutoff2;
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

            List<IdDouble<I>> list = recommendation.getItems();

            IntList perm = new IntArrayList();
            IntLinkedOpenHashSet remainingI = new IntLinkedOpenHashSet();
            IntStream.range(0, list.size()).forEach(i -> remainingI.add(i));

            while (!remainingI.isEmpty() && perm.size() < cutoff1) {
                int bestI = selectItem(remainingI, list);

                perm.add(bestI);
                remainingI.remove(bestI);

                update(list.get(bestI));
            }

            while (perm.size() < min(cutoff2, list.size())) {
                perm.add(remainingI.removeFirstInt());
            }

            return perm.toIntArray();
        }

        protected int selectItem(IntSortedSet remainingI, List<IdDouble<I>> list) {
            double[] max = new double[]{Double.NEGATIVE_INFINITY};
            int[] bestI = new int[]{remainingI.firstInt()};
            remainingI.forEach(i -> {
                double value = value(list.get(i));
                if (isNaN(value)) {
                    return;
                }
                if (value > max[0] || (value == max[0] && i < bestI[0])) {
                    max[0] = value;
                    bestI[0] = i;
                }
            });

            return bestI[0];
        }

        protected abstract double value(IdDouble<I> itemValue);

        protected abstract void update(IdDouble<I> bestItemValue);
    }

}
