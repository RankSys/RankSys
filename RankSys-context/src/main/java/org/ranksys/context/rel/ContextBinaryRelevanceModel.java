/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rel;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;

/**
 * Context-aware binary relevance model.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 *
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public class ContextBinaryRelevanceModel<U, I, C> extends ContextGradedRelevanceModel<U, I, C> {

    /**
     * Constructor.
     *
     * @param testData test preferences
     * @param threshold relevance threshold
     */
    public ContextBinaryRelevanceModel(PreferenceData<U, I> testData, double threshold) {
        super(testData, v -> v >= threshold ? 1.0 : 0.0);
    }

}
