/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.intentaware.reranking;

import org.ranksys.novdiv.intentaware.AspectModel;

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
public class XQuAD<U, I, F> extends AlphaXQuAD<U, I, F> {

    /**
     * Constructor.
     *
     * @param aspectModel intent-aware model
     * @param lambda trade-off between novelty and relevance
     * @param cutoff number of items to be greedily selected
     * @param norm normalize the linear combination between relevance and 
     * novelty
     */
    public XQuAD(AspectModel<U, I, F> aspectModel, double lambda, int cutoff, boolean norm) {
        super(aspectModel, 1.0, lambda, cutoff, norm);
    }

}
