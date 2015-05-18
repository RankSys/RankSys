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
