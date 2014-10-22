/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import static es.uam.eps.ir.ranksys.diversity.reranking.PermutationReranker.getBasePerm;
import es.uam.eps.ir.ranksys.core.util.Stats;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import static java.lang.Math.min;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public abstract class LambdaReranker<U, I> extends GreedyReranker<U, I> {

    protected final double lambda;
    private final boolean norm;

    public LambdaReranker(double lambda, int cutoff, boolean norm) {
        super(cutoff);
        this.lambda = lambda;
        this.norm = norm;
    }

    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation) {
        if (lambda == 0.0) {
            return getBasePerm(min(cutoff, recommendation.getItems().size()));
        } else {
            return super.rerankPermutation(recommendation);
        }
    }

    @Override
    protected abstract GreedyUserReranker<U, I> getUserReranker(Recommendation<U, I> recommendation);

    protected abstract class LambdaUserReranker extends GreedyUserReranker<U, I> {

        protected Stats relStats;
        protected Stats novStats;
        protected TObjectDoubleMap<I> novMap;

        public LambdaUserReranker(Recommendation<U, I> recommendation) {
            super(recommendation);
        }

        protected double normRel(double rel) {
            if (norm) {
                return (rel - relStats.getMean()) / relStats.getStandardDeviation();
            } else {
                return rel;
            }
        }

        protected double normNov(double nov) {
            if (norm) {
                return (nov - novStats.getMean()) / novStats.getStandardDeviation();
            } else {
                return nov;
            }
        }

        @Override
        protected IdDoublePair<I> selectItem(U user, List<IdDoublePair<I>> remaining, List<IdDoublePair<I>> reranked) {
            novMap = new TObjectDoubleHashMap<>();
            relStats = new Stats();
            novStats = new Stats();
            remaining.forEach(itemValue -> {
                double nov = nov(user, itemValue, reranked);
                novMap.put(itemValue.id, nov);
                relStats.increment(itemValue.v);
                novStats.increment(nov);
            });
            return super.selectItem(user, remaining, reranked);
        }

        @Override
        protected double value(U user, IdDoublePair<I> iv, List<IdDoublePair<I>> reranked) {
            return (1 - lambda) * normRel(iv.v) + lambda * normNov(novMap.get(iv.id));
        }

        protected abstract double nov(U user, IdDoublePair<I> iv, List<IdDoublePair<I>> reranked);

    }
}
