/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.novelty.inverted.br;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.novdiv.reranking.PermutationReranker;
import java.util.List;

/**
 *
 * @author saul
 */
public abstract class BayesRuleReranker<U, I> extends PermutationReranker<U, I> {

    public BayesRuleReranker() {
    }

    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation, int maxLength) {
        int N = maxLength;
        if (maxLength == 0) {
            N = recommendation.getItems().size();
        }

        IntDoubleTopN topN = new IntDoubleTopN(N);
        List<IdDouble<I>> list = recommendation.getItems();
        int M = list.size();
        for (int i = 0; i < list.size(); i++) {
            topN.add(M - i, likelihood(list.get(i)) * prior(list.get(i).id));
        }
        topN.sort();

        int[] perm = topN.reverseStream()
                .mapToInt(e -> M - e.getIntKey())
                .toArray();

        return perm;
    }

    protected abstract double likelihood(IdDouble<I> iv);

    protected abstract double prior(I i);
}
