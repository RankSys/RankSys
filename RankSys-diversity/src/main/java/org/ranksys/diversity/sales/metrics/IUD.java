/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.diversity.sales.metrics;

import org.ranksys.metrics.rank.NoDiscountModel;
import org.ranksys.metrics.rel.NoRelevanceModel;

/**
 * Inter-user diversity. It is actually a relevance and rank-unaware version of
 * {@link EIUDC}.
 *
 * A. Bellogín, I. Cantador and P. Castells.  A study of heterogeneity in
 * recommendations for social music service. HetRec 2010.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class IUD<U, I> extends EIUDC<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendation lists that is evaluated
     */
    public IUD(int cutoff) {
        super(cutoff, new NoDiscountModel(), new NoRelevanceModel<>());
    }

}
