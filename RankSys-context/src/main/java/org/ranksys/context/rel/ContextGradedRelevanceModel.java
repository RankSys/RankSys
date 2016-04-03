/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.rel;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel.UserIdealRelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import org.jooq.lambda.tuple.Tuple2;
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
public class ContextGradedRelevanceModel<U, I, C> extends IdealRelevanceModel<Tuple2<U, C>, I> {

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
    protected UserIdealRelevanceModel<Tuple2<U, C>, I> get(Tuple2<U, C> userCtx) {
        return new UserContextGradedRelevanceModel(userCtx);
    }

    private class UserContextGradedRelevanceModel implements UserIdealRelevanceModel<Tuple2<U, C>, I> {

        private final Object2DoubleMap<I> relevantItems;

        public UserContextGradedRelevanceModel(Tuple2<U, C> userCtx) {
            this.relevantItems = new Object2DoubleOpenHashMap<>();
            testData.getUserPreferences(userCtx.v1)
                    .filter(p -> rel.applyAsDouble(p.v2) > 0)
                    .filter(p -> ((IdPrefCtx<I, C>) p).cs.contains(userCtx.v2))
                    .forEach(p -> relevantItems.put(p.v1, rel.applyAsDouble(p.v2)));
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
