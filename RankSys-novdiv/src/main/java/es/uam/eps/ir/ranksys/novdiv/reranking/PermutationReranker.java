/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.reranking;

import es.uam.eps.ir.ranksys.core.Recommendation;
import java.util.ArrayList;
import java.util.List;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;

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
        List<Tuple2od<I>> from = recommendation.getItems();
        List<Tuple2od<I>> to = new ArrayList<>();
        for (int i = 0; i < perm.length; i++) {
            to.add(tuple(from.get(perm[i]).v1, (double) (perm.length - i)));
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
