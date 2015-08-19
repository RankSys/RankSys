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
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract re-ranker whose output is a permutation of the input
 * recommendation. Convenient for storing the minimum required information.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class PermutationReranker<U, I> implements Reranker<U, I> {

    /**
     * Returns the permutation that is applied to the input recommendation
     * to generate the re-ranked recommendation.
     *
     * @param recommendation input recommendation
     * @param maxLength maximum length of the permutation
     * @return permutation that encodes the re-ranking
     */
    public abstract int[] rerankPermutation(Recommendation<U, I> recommendation, int maxLength);

    @Override
    public Recommendation<U, I> rerankRecommendation(Recommendation<U, I> recommendation, int maxLength) {
        int[] perm = rerankPermutation(recommendation, maxLength);

        return permuteRecommendation(recommendation, perm);
    }

    /**
     * Applies a permutation to re-rank a recommendation.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param recommendation input recommendation
     * @param perm permutation
     * @return re-ranked recommendation according to the permutation
     */
    public static <U, I> Recommendation<U, I> permuteRecommendation(Recommendation<U, I> recommendation, int[] perm) {
        List<IdDouble<I>> from = recommendation.getItems();
        List<IdDouble<I>> to = new ArrayList<>();
        for (int i = 0; i < perm.length; i++) {
            to.add(new IdDouble<>(from.get(perm[i]).id, (double) (perm.length - i)));
        }

        return new Recommendation<>(recommendation.getUser(), to);
    }

    /**
     * Returns a null permutation, that is, an array with values 0..(N-1).
     *
     * @param n size of the permutation
     * @return null permutation
     */
    protected static int[] getBasePerm(int n) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }

        return perm;
    }
}
