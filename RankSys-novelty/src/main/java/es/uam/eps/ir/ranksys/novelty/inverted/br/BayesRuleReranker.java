/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.inverted.br;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.novdiv.reranking.PermutationReranker;
import java.util.List;

/**
 * Bayesian probabilistic reformulation re-ranker.
 * 
 * s(u, i) = likelihood(u | i) * prior(i)
 *
 * S. Vargas and P. Castells. Improving sales diversity by recommending
 * users to items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class BayesRuleReranker<U, I> extends PermutationReranker<U, I> {

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

    /**
     * Returns the likelihood of an item: p(u | i).
     *
     * @param iv item-relevance pair
     * @return likelihood
     */
    protected abstract double likelihood(IdDouble<I> iv);

    /**
     * Returns the prior of an item: p(i).
     *
     * @param i item
     * @return prior
     */
    protected abstract double prior(I i);
}
