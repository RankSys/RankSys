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

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public abstract class PermutationReranker<U, I> implements Reranker<U, I> {

    public abstract int[] rerankPermutation(Recommendation<U, I> recommendation);

    @Override
    public Recommendation<U, I> rerankRecommendation(Recommendation<U, I> recommendation) {
        int[] perm = rerankPermutation(recommendation);

        return permuteRecommendation(recommendation, perm);
    }

    public static <U, I> Recommendation<U, I> permuteRecommendation(Recommendation<U, I> recommendation, int[] perm) {
        List<IdDouble<I>> from = recommendation.getItems();
        List<IdDouble<I>> to = new ArrayList<>();
        for (int i = 0; i < perm.length; i++) {
            to.add(new IdDouble<>(from.get(perm[i]).id, (double) (perm.length - i)));
        }

        return new Recommendation<>(recommendation.getUser(), to);
    }

    protected static int[] getBasePerm(int n) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }

        return perm;
    }
}
