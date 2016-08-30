/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware.reranking;

import org.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.intentaware.AspectModel;
import org.ranksys.novdiv.reranking.LambdaReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * eXplicit Query Aspect Diversification re-ranker with parametrised tolerance
 * to redundancy.
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
public class AlphaXQuAD<U, I, F> extends LambdaReranker<U, I> {

    /**
     * Aspect model used for diversification.
     */
    protected final AspectModel<U, I, F> aspectModel;
    private final double alpha;

    /**
     * Constructor.
     *
     * @param aspectModel intent-aware model
     * @param alpha tolerance to redundancy parameter
     * @param lambda trade-off between novelty and relevance
     * @param cutoff number of items to be greedily selected
     * @param norm normalize the linear combination between relevance and 
     * novelty
     */
    public AlphaXQuAD(AspectModel<U, I, F> aspectModel, double alpha, double lambda, int cutoff, boolean norm) {
        super(lambda, cutoff, norm);
        this.aspectModel = aspectModel;
        this.alpha = alpha;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new UserAlphaXQuAD(recommendation, maxLength);
    }

    /**
     * User re-ranker for {@link AlphaXQuAD}.
     */
    protected class UserAlphaXQuAD extends LambdaUserReranker {

        private final AspectModel<U, I, F>.UserAspectModel uam;
        private final Object2DoubleOpenHashMap<F> redundancy;

        /**
         * Constructor.
         *
         * @param recommendation input recommendation to be re-ranked
         * @param maxLength maximum length to be re-ranked with xQuAD
         */
        public UserAlphaXQuAD(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            this.uam = aspectModel.getModel(recommendation.getUser());
            this.uam.initializeWithItems(recommendation.getItems());
            this.redundancy = new Object2DoubleOpenHashMap<>();
            this.redundancy.defaultReturnValue(1.0);
        }

        @Override
        protected double nov(Tuple2od<I> iv) {
            return uam.getItemIntents(iv.v1)
                    .mapToDouble(f -> {
                        return uam.pf_u(f) * uam.pi_f(iv, f) * redundancy.getDouble(f);
                    }).sum();
        }

        @Override
        protected void update(Tuple2od<I> biv) {
            uam.getItemIntents(biv.v1).sequential()
                    .forEach(f -> {
                        redundancy.put(f, redundancy.getDouble(f) * (1 - alpha * uam.pi_f(biv, f)));
                    });
        }

    }

}
