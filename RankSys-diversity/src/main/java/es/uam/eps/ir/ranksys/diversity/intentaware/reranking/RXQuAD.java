/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel.UserIntentModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.LambdaReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

/**
 * Relevance-based eXplicit Query Aspect Diversification re-ranker.
 * 
 * S. Vargas, P. Castells and D. Vallet. Explicit relevance models in 
 * intent-oriented Information Retrieval diversification. SIGIR 2012.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class RXQuAD<U, I, F> extends LambdaReranker<U, I> {

    private final IntentModel<U, I, F> intentModel;
    private final double alpha;

    /**
     * Constructor.
     *
     * @param intentModel intent-aware model
     * @param alpha tolerance to redundancy parameter
     * @param lambda trade-off between novelty and relevance
     * @param cutoff number of items to be greedily selected
     * @param norm normalize the linear combination between relevance and 
     * novelty
     */
    public RXQuAD(IntentModel<U, I, F> intentModel, double alpha, double lambda, int cutoff, boolean norm) {
        super(lambda, cutoff, norm);
        this.intentModel = intentModel;
        this.alpha = alpha;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new UserRXQuAD(recommendation, maxLength);
    }

    /**
     * User re-ranker for {@link RXQuAD}.
     */
    protected class UserRXQuAD extends LambdaUserReranker {

        private final UserIntentModel<U, I, F> uim;
        private final Object2DoubleOpenHashMap<F> redundancy;
        private final Object2DoubleOpenHashMap<F> probNorm;

        /**
         * Constructor.
         *
         * @param recommendation input recommendation to be re-ranked
         * @param maxLength maximum length to be re-ranked with xQuAD
         */
        public UserRXQuAD(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            this.uim = intentModel.getModel(recommendation.getUser());
            this.redundancy = new Object2DoubleOpenHashMap<>();
            this.redundancy.defaultReturnValue(1.0);
            this.probNorm = new Object2DoubleOpenHashMap<>();
            recommendation.getItems().forEach(iv -> {
                uim.getItemIntents(iv.id).sequential().forEach(f -> {
                    if (!probNorm.containsKey(f)) {
                        probNorm.put(f, iv.v);
                    }
                });
            });
        }

        private double pif(IdDouble<I> iv, F f) {
            return (Math.pow(2, iv.v / probNorm.getDouble(f)) - 1) / 2.0;
        }

        @Override
        protected double nov(IdDouble<I> iv) {
            return uim.getItemIntents(iv.id)
                    .mapToDouble(f -> {
                        return uim.p(f) * pif(iv, f) * redundancy.getDouble(f);
                    }).sum();
        }

        @Override
        protected void update(IdDouble<I> biv) {
            uim.getItemIntents(biv.id).sequential()
                    .forEach(f -> {
                        redundancy.put(f, redundancy.getDouble(f) * (1 - alpha * pif(biv, f)));
                    });
        }

    }

}
