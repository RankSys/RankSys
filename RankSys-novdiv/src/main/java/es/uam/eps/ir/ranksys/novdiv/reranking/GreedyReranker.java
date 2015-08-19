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
 * Greedy re-ranking. Greedily selects items from an input recommendation
 * according to a selection criterion that is updated after a selection. It
 * requires a nested {@link GreedyUserReranker}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class GreedyReranker<U, I> extends PermutationReranker<U, I> {

    /**
     * Cut-off of the re-ranking.
     */
    protected final int cutoff;

    /**
     * Constructor.
     *
     * @param cutoff how many items are re-ranked by the greedy selection.
     */
    public GreedyReranker(int cutoff) {
        this.cutoff = cutoff;
    }

    /**
     * Returns the permutation that is applied to the input recommendation
     * to generate the re-ranked recommendation.
     *
     * @param recommendation input recommendation
     * @param maxLength maximum length of the permutation
     * @return permutation that encodes the re-ranking
     */
    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation, int maxLength) {
        return getUserReranker(recommendation, maxLength).rerankPermutation();
    }

    /**
     * Returns an instance of {@link GreedyUserReranker} that does the greedy
     * selection.
     *
     * @param recommendation input recommendation to be re-ranked
     * @param maxLength maximum length of the resulting re-ranked recommendation
     * @return the {@link GreedyUserReranker} that does the re-ranking
     */
    protected abstract GreedyUserReranker<U, I> getUserReranker(Recommendation<U, I> recommendation, int maxLength);

    /**
     * Re-ranker of a single recommendation.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     */
    protected abstract class GreedyUserReranker<U, I> {

        /**
         * input recommendation
         */
        protected final Recommendation<U, I> recommendation;

        /**
         * maximum length of the re-ranked recommendation
         */
        protected final int maxLength;

        /**
         * Constructor
         *
         * @param recommendation input recommendation
         * @param maxLength maximum length of the re-ranked recommendation
         */
        public GreedyUserReranker(Recommendation<U, I> recommendation, int maxLength) {
            this.recommendation = recommendation;
            this.maxLength = maxLength;
        }

        /**
         * Returns the permutation to obtain the re-ranking
         *
         * @return permutation to obtain the re-ranking
         */
        public int[] rerankPermutation() {

            List<IdDouble<I>> list = recommendation.getItems();

            IntList perm = new IntArrayList();
            IntLinkedOpenHashSet remainingI = new IntLinkedOpenHashSet();
            IntStream.range(0, list.size()).forEach(i -> remainingI.add(i));

            while (!remainingI.isEmpty() && perm.size() < min(maxLength, cutoff)) {
                int bestI = selectItem(remainingI, list);

                perm.add(bestI);
                remainingI.remove(bestI);

                update(list.get(bestI));
            }

            while (perm.size() < min(maxLength, list.size())) {
                perm.add(remainingI.removeFirstInt());
            }

            return perm.toIntArray();
        }

        /**
         * Selects the next element of the permutation that maximizes the 
         * objective function.
         *
         * @param remainingI positions of the original recommendation that have
         * not been selected yet.
         * @param list the list of item-score pairs of the input recommendation
         * @return the next element of the permutation that maximizes the 
         * objective function.
         */
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

        /**
         * Objective function that drives the greedy selection.
         *
         * @param itemValue item-score pair of the input recommendation.
         * @return value of the function with the given item-score pair.
         */
        protected abstract double value(IdDouble<I> itemValue);

        /**
         * Updates the value of the objective function after a selection.
         *
         * @param bestItemValue item-score pair that has been selected to
         * be added to the re-ranking
         */
        protected abstract void update(IdDouble<I> bestItemValue);
    }

}
