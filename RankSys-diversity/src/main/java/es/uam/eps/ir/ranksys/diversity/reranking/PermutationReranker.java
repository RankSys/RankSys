/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saul
 */
public abstract class PermutationReranker<U, I> implements Reranker<U, I>{

    public abstract int[] rerankPermutation(Recommendation<U, I> recommendation);

    @Override
    public Recommendation<U, I> rerankRecommendation(Recommendation<U, I> recommendation) {
        int[] perm = rerankPermutation(recommendation);

        return permuteRecommendation(recommendation, perm);
    }
    
    public static <U, I> Recommendation<U, I> permuteRecommendation(Recommendation<U, I> recommendation, int[] perm) {
        List<IdDoublePair<I>> from = recommendation.getItems();
        List<IdDoublePair<I>> to = new ArrayList<>();
        for (int i = 0; i < perm.length; i++) {
            to.add(new IdDoublePair<>(from.get(perm[i]).id, (double) (perm.length - i)));
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
