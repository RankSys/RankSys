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
import es.uam.eps.ir.ranksys.rec.Recommender;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import static java.lang.Math.pow;
import java.util.stream.Stream;

/**
 * Prior smoothed Bayesian probabilistic reformulation re-ranker. Estimation of
 * likelihood and prior are based on the relevance scores of the recommender.
 * 
 * S. Vargas and P. Castells. Improving sales diversity by recommending
 * users to items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class AlphaBayesRuleReranker<U, I> extends BayesRuleReranker<U, I> {

    private final double alpha;
    private final Object2DoubleMap<I> norm;

    /**
     * Constructor that receives a pre-calculated norm map.
     *
     * @param alpha smoothing of the prior
     * @param norm norm map
     */
    public AlphaBayesRuleReranker(double alpha, Object2DoubleMap<I> norm) {
        super();
        this.alpha = alpha;
        this.norm = norm;
    }

    /**
     * Constructor that receives a set of recommendations and calculates the
     * norm.
     *
     * @param alpha smoothing of the prior
     * @param recommendations previously calculated recommendations
     */
    public AlphaBayesRuleReranker(double alpha, Stream<Recommendation<U, I>> recommendations) {
        this(alpha, calculateNorm(recommendations));
    }
    
    /**
     * Constructor that generates recommendations to calculate a norm.
     *
     * @param alpha smoothing of the prior
     * @param users set of users to generate recommendations to calculate the norm
     * @param recommender recommender whose norm is used
     */
    public AlphaBayesRuleReranker(double alpha, Stream<U> users, Recommender<U, I> recommender) {
        this(alpha, users.parallel().map(u -> recommender.getRecommendation(u, 0)));
    }

    /**
     * Calculates the norm (sum of relevance scores) of each item.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param recommendations set of recommendations used to calculate the norm
     * @return item-norm map
     */
    public static <U, I> Object2DoubleMap<I> calculateNorm(Stream<Recommendation<U, I>> recommendations) {
        return recommendations.parallel()
                .flatMap(recommendation -> recommendation.getItems().stream())
                .collect(
                        () -> new Object2DoubleOpenHashMap<I>(),
                        (m, iv) -> m.addTo(iv.id, iv.v),
                        (m1, m2) -> m2.object2DoubleEntrySet().forEach(e -> m1.addTo(e.getKey(), e.getDoubleValue()))
                );
    }

    @Override
    protected double likelihood(IdDouble<I> iv) {
        return iv.v / norm.get(iv.id);
    }

    @Override
    protected double prior(I i) {
        return pow(norm.get(i), 1.0 - alpha);
    }

}
