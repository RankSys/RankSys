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

import es.uam.eps.ir.ranksys.core.Recommendation;
import static java.lang.Math.min;
import java.util.Random;

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
