/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.LogLikelihoodSimilarity;

/**
 * Log-likelihood item similarity.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * 
 * @param <I> type of the items
 */
public class LogLikelihoodItemSimilarity<I> extends ItemSimilarity<I> {

    /**
     * Constructor.
     *
     * @param preferences preference
     * @param dense true for array-based calculations, false to map-based
     */
    public LogLikelihoodItemSimilarity(FastPreferenceData<?, I> preferences, boolean dense) {
        super(preferences, new LogLikelihoodSimilarity(new TransposedPreferenceData<>(preferences), dense));
    }

}
