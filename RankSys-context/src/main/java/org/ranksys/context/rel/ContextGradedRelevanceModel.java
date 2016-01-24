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
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import org.ranksys.context.pref.ContextPreferenceData.IdPrefCtx;

/**
 * Context-aware graded relevance model.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 *
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public class ContextGradedRelevanceModel<U, I, C> extends IdealRelevanceModel<IdObject<U, C>, I> {

    private final PreferenceData<U, I> testData;
    private final DoubleUnaryOperator rel;

    /**
     * Constructor.
     *
     * @param testData test subset of the preferences
     * @param rel graded relevance function
     */
    public ContextGradedRelevanceModel(PreferenceData<U, I> testData, DoubleUnaryOperator rel) {
        super();
        this.testData = testData;
        this.rel = rel;
    }

    @Override
    protected UserIdealRelevanceModel<IdObject<U, C>, I> get(IdObject<U, C> userCtx) {
        return new UserContextGradedRelevanceModel(userCtx);
    }

    private class UserContextGradedRelevanceModel implements UserIdealRelevanceModel<IdObject<U, C>, I> {

        private final Object2DoubleMap<I> relevantItems;

        public UserContextGradedRelevanceModel(IdObject<U, C> userCtx) {
            this.relevantItems = new Object2DoubleOpenHashMap<>();
            testData.getUserPreferences(userCtx.id)
                    .filter(p -> rel.applyAsDouble(p.v) > 0)
                    .filter(p -> ((IdPrefCtx<I, C>) p).cs.contains(userCtx.v))
                    .forEach(p -> relevantItems.put(p.id, rel.applyAsDouble(p.v)));
        }

        @Override
        public Set<I> getRelevantItems() {
            return relevantItems.keySet();
        }

        @Override
        public boolean isRelevant(I item) {
            return relevantItems.containsKey(item);
        }

        @Override
        public double gain(I item) {
            return relevantItems.getDouble(item);
        }

    }
}
