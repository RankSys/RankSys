/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rel;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel.UserIdealRelevanceModel;
import java.util.Set;
import java.util.stream.Collectors;
import org.ranksys.context.pref.ContextPreferenceData.IdPrefCtx;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public class ContextBinaryRelevanceModel<U, I, C> extends IdealRelevanceModel<IdObject<U, C>, I> {

    private final PreferenceData<U, I> testData;
    private final double threshold;

    /**
     * Constructor
     *
     * @param testData test subset of the preferences
     * @param threshold relevance threshold
     */
    public ContextBinaryRelevanceModel(PreferenceData<U, I> testData, double threshold) {
        super();
        this.testData = testData;
        this.threshold = threshold;
    }

    @Override
    protected UserIdealRelevanceModel<IdObject<U, C>, I> get(IdObject<U, C> userCtx) {
        return new UserContextBinaryRelevanceModel(userCtx);
    }

    private class UserContextBinaryRelevanceModel implements UserIdealRelevanceModel<IdObject<U, C>, I> {

        private final Set<I> relevantItems;

        public UserContextBinaryRelevanceModel(IdObject<U, C> userCtx) {
            this.relevantItems = testData.getUserPreferences(userCtx.id)
                    .filter(iv -> iv.v >= threshold)
                    .filter(p -> ((IdPrefCtx<I, C>) p).cs.contains(userCtx.v))
                    .map(iv -> iv.id)
                    .collect(Collectors.toSet());
        }

        @Override
        public Set<I> getRelevantItems() {
            return relevantItems;
        }

        @Override
        public boolean isRelevant(I item) {
            return relevantItems.contains(item);
        }

        @Override
        public double gain(I item) {
            return isRelevant(item) ? 1.0 : 0.0;
        }

    }
}
