/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.user.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.LogLikelihoodSimilarity;

/**
 * Log-likelihood user similarity.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 * 
 * @param <U> type of the users
 */
public class LogLikelihoodUserSimilarity<U> extends UserSimilarity<U> {
    
    /**
     * Constructor.
     *
     * @param preferences preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public LogLikelihoodUserSimilarity(FastPreferenceData<U, ?> preferences, boolean dense) {
        super(preferences, new LogLikelihoodSimilarity(preferences, dense));
    }
    
}
