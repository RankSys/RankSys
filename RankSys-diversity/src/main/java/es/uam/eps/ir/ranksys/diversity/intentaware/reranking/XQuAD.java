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
 * eXplicit Query Aspect Diversification re-ranker.
 * 
 * S. Vargas, P. Castells and D. Vallet. Intent-oriented diversity in 
 * Recommender Systems. SIGIR 2011.
 * 
 * R.L.T. Santos, C. Macdonald and I. Ounis. Exploiting query reformulations
 * for Web search result diversification. WWW 2010.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class XQuAD<U, I, F> extends LambdaReranker<U, I> {

    private final IntentModel<U, I, F> intentModel;

    /**
     * Constructor.
     *
     * @param intentModel intent-aware model
     * @param lambda trade-off between novelty and relevance
     * @param cutoff number of items to be greedily selected
     * @param norm normalize the linear combination between relevance and 
     * novelty
     */
    public XQuAD(IntentModel<U, I, F> intentModel, double lambda, int cutoff, boolean norm) {
        super(lambda, cutoff, norm);
        this.intentModel = intentModel;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation, int maxLength) {
        return new UserXQuAD(recommendation, maxLength);
    }

    /**
     * User re-ranker for {@link XQuAD}.
     */
    protected class UserXQuAD extends LambdaUserReranker {

        private final UserIntentModel<U, I, F> uim;
        private final Object2DoubleOpenHashMap<F> redundancy;
        private final Object2DoubleOpenHashMap<F> probNorm;

        /**
         * Constructor.
         *
         * @param recommendation input recommendation to be re-ranked
         * @param maxLength maximum length to be re-ranked with xQuAD
         */
        public UserXQuAD(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);

            this.uim = intentModel.getModel(recommendation.getUser());
            this.redundancy = new Object2DoubleOpenHashMap<>();
            this.redundancy.defaultReturnValue(1.0);
            this.probNorm = new Object2DoubleOpenHashMap<>();
            recommendation.getItems().forEach(iv -> {
                uim.getItemIntents(iv.id).sequential().forEach(f -> {
                    probNorm.addTo(f, iv.v);
                });
            });
        }

        private double pif(IdDouble<I> iv, F f) {
            return iv.v / probNorm.getDouble(f);
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
                        redundancy.put(f, redundancy.getDouble(f) * (1 - pif(biv, f)));
                    });
        }

    }

}
