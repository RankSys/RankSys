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
import es.uam.eps.ir.ranksys.rec.Recommender;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import static java.lang.Math.pow;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2od;

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
        this(alpha, users.parallel().map(u -> recommender.getRecommendation(u)));
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
                        (m, iv) -> m.addTo(iv.v1, iv.v2),
                        (m1, m2) -> m2.object2DoubleEntrySet().forEach(e -> m1.addTo(e.getKey(), e.getDoubleValue()))
                );
    }

    @Override
    protected double likelihood(Tuple2od<I> iv) {
        return iv.v2 / norm.get(iv.v1);
    }

    @Override
    protected double prior(I i) {
        return pow(norm.get(i), 1.0 - alpha);
    }

}
