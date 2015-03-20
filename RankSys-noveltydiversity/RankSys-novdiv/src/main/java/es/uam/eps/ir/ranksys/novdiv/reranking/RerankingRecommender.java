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
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class RerankingRecommender<U, I> implements Recommender<U, I> {

    private final Recommender<U, I> recommender;
    private final Reranker<U, I> reranker;

    public RerankingRecommender(Recommender<U, I> recommender, Reranker<U, I> reranker) {
        this.recommender = recommender;
        this.reranker = reranker;
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, int maxLength) {
        return reranker.rerankRecommendation(recommender.getRecommendation(u, maxLength));
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter) {
        return reranker.rerankRecommendation(recommender.getRecommendation(u, maxLength, filter));
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates) {
        return reranker.rerankRecommendation(recommender.getRecommendation(u, candidates));
    }
}
