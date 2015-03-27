/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author saul
 */
public class AlphaBayesRuleReranker<U, I> extends BayesRuleReranker<U, I> {

    private final double alpha;
    private final Object2DoubleMap<I> norm;

    public AlphaBayesRuleReranker(double alpha, Object2DoubleMap<I> norm) {
        super();
        this.alpha = alpha;
        this.norm = norm;
    }

    public AlphaBayesRuleReranker(double alpha, Stream<Recommendation<U, I>> recommendations) {
        this(alpha, calculateNorm(recommendations));
    }
    
    public AlphaBayesRuleReranker(double alpha, Stream<U> users, Recommender<U, I> recommender) {
        this(alpha, users.parallel().map(u -> recommender.getRecommendation(u, 0)));
    }

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
