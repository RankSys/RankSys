/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.reranking;

import es.uam.eps.ir.ranksys.core.Recommendation;

/**
 * Re-ranker. Changes the position of items in a recommendation list to
 * optimize criteria other than relevance.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface Reranker<U, I> {

    /**
     * Re-ranks a recommendation.
     *
     * @param recommendation recommendation to be re-ranked
     * @param maxLength maximum length of the re-ranking
     * @return a recommendation that is a re-ranking of the input one
     */
    Recommendation<U, I> rerankRecommendation(Recommendation<U, I> recommendation, int maxLength);
}
