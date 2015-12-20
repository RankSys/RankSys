/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.distance.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.LambdaReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.function.ToDoubleFunction;

/**
 * Maximum marginal relevance re-ranker.
 *
 * C.-N. Ziegler, S.M. McNee, J.A. Konstan and G. Lausen. Improving recommendation lists through topic diversification. WWW 2005.
 *
 * J. Carbonell and J. Goldstein. The use of MMR, diversity-based reranking for reordering documents and producing summaries. SIGIR 1998.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class MMR<U, I> extends LambdaReranker<U, I> {

    private final ItemDistanceModel<I> dist;

    /**
     * Constructor.
     *
     * @param lambda trade-off parameter of the linear combination
     * @param cutoff how many items are re-ranked by the greedy selection.
     * @param dist item distance model
     */
    public MMR(double lambda, int cutoff, ItemDistanceModel<I> dist) {
        super(lambda, cutoff, true);

        this.dist = dist;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new UserMMR(recommendation, maxLength);
    }

    /**
     * User re-ranker for {@link MMR}.
     */
    public class UserMMR extends LambdaUserReranker {

        private final Object2DoubleOpenHashMap<I> avgDist;
        private int n;

        /**
         * Constructor.
         *
         * @param recommendation input recommendation to be re-ranked
         * @param maxLength maximum length of the re-ranked recommendation
         */
        public UserMMR(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            n = 0;
            avgDist = new Object2DoubleOpenHashMap<>();
            avgDist.defaultReturnValue(0.0);
            recommendation.getItems().stream().sequential()
                    .map(iv -> iv.id)
                    .forEach(i -> avgDist.put(i, 0.0));
        }

        @Override
        protected double nov(IdDouble<I> itemValue) {
            return avgDist.getDouble(itemValue.id);
        }

        @Override
        protected void update(IdDouble<I> bestItemValue) {
            I bestItem = bestItemValue.id;
            ToDoubleFunction<I> bDist = dist.dist(bestItem);
            avgDist.remove(bestItem);

            n++;
            avgDist.object2DoubleEntrySet().forEach(e -> {
                I i = e.getKey();
                double d = e.getDoubleValue();
                double d2 = bDist.applyAsDouble(i);
                avgDist.addTo(i, (d2 - d) / n);
            });
        }

    }
}
