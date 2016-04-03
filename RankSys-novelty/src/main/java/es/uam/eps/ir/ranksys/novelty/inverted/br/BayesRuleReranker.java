/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.inverted.br;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.novdiv.reranking.PermutationReranker;
import static java.lang.Math.min;
import java.util.List;
import org.ranksys.core.util.tuples.Tuple2od;

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
        List<Tuple2od<I>> items = recommendation.getItems();
        int M = items.size();
        int N = min(maxLength, M);

        IntDoubleTopN topN = new IntDoubleTopN(N);
        for (int i = 0; i < M; i++) {
            topN.add(M - i, likelihood(items.get(i)) * prior(items.get(i).v1));
        }
        topN.sort();

        int[] perm = topN.reverseStream()
                .mapToInt(e -> M - e.v1)
                .toArray();

        return perm;
    }

    /**
     * Returns the likelihood of an item: p(u | i).
     *
     * @param iv item-relevance pair
     * @return likelihood
     */
    protected abstract double likelihood(Tuple2od<I> iv);

    /**
     * Returns the prior of an item: p(i).
     *
     * @param i item
     * @return prior
     */
    protected abstract double prior(I i);
}
