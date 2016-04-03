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
import static java.lang.Math.min;
import java.util.Random;
import static java.lang.Math.min;
import static java.lang.Math.min;
import static java.lang.Math.min;

/**
 * Re-ranker that applies a random permutation.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class RandomReranker<U, I> extends PermutationReranker<U, I> {

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
        int n = min(maxLength, recommendation.getItems().size());

        int[] perm = getBasePerm(n);

        Random rnd = new Random();
        for (int i = n - 1; i >= 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = perm[index];
            perm[index] = perm[i];
            perm[i] = a;
        }
        
        return perm;
    }
    
}
