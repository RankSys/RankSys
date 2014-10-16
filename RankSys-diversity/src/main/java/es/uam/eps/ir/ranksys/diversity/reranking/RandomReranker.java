/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.diversity.reranking;

import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import java.util.Random;

/**
 *
 * @author saul
 */
public class RandomReranker<U, I> extends PermutationReranker<U, I> {

    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation) {
        int n = recommendation.getItems().size();

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
