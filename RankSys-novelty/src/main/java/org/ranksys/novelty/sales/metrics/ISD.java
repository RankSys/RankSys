/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novelty.sales.metrics;

import org.ranksys.metrics.rank.NoDiscountModel;
import org.ranksys.metrics.rel.NoRelevanceModel;
import org.ranksys.novelty.sales.ISDCItemNovelty;
import java.util.List;
import java.util.function.Function;

/**
 * Inter-system diversity. It is actually a relevance and rank-unaware version of
 * {@link EISDC}.
 *
 * A. Bellogín, I. Cantador and P. Castells.  A study of heterogeneity in
 * recommendations for social music service. HetRec 2010.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ISD<U, I> extends EISDC<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum size of the recommendation list that is evaluated
     * @param nSystems number of compared systems
     * @param otherUserRecommendations other systems recommendations
     */
    public ISD(int cutoff, int nSystems, Function<U, List<List<I>>> otherUserRecommendations) {
        super(cutoff, new ISDCItemNovelty<>(nSystems, otherUserRecommendations, new NoDiscountModel()), new NoRelevanceModel<>(), new NoDiscountModel());
    }
    
}
