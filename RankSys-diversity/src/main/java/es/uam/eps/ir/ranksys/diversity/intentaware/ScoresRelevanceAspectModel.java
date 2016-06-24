/*
 * Copyright (C) 2015 RankSys (http://ranksys.org)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

/**
 * Relevance-based aspect model for eXplicit Query Aspect Diversification
 * re-ranker.
 *
 * S. Vargas, P. Castells and D. Vallet. Explicit relevance models in
 * intent-oriented Information Retrieval diversification. SIGIR 2012.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 */
public class ScoresRelevanceAspectModel<U, I, F> extends AspectModel<U, I, F> {

    private final PreferenceData<U, I> scoresData;

    /**
     * Constructor taking an intent model and scores data.
     *
     * @param intentModel intent model
     * @param scoresData scores data
     */
    public ScoresRelevanceAspectModel(IntentModel<U, I, F> intentModel, PreferenceData<U, I> scoresData) {
        super(intentModel);
        this.scoresData = scoresData;
    }

    @Override
    protected UserAspectModel get(U u) {
        return new ScoresUserRelevanceAspectModel(u);
    }

    /**
     * User aspect model for {@link ScoresRelevanceAspectModel}.
     */
    public class ScoresUserRelevanceAspectModel extends UserAspectModel {

        private final Object2DoubleOpenHashMap<F> probNorm;
        private final Object2DoubleOpenHashMap<I> scores;

        /**
         * Constructor.
         *
         * @param user user
         */
        public ScoresUserRelevanceAspectModel(U user) {
            super(user);
            this.probNorm = new Object2DoubleOpenHashMap<>();
            this.scores = new Object2DoubleOpenHashMap<>();
            scoresData.getUserPreferences(user).forEach(iv -> {
                getItemIntents(iv.v1).sequential().forEach(f -> {
                    if (iv.v2 > probNorm.getOrDefault(f, 0.0)) {
                        probNorm.put(f, iv.v2);
                    }
                });
                scores.put(iv.v1, iv.v2);
            });
        }

        @Override
        public double pi_f(I i, F f) {
            return (Math.pow(2, scores.get(i) / probNorm.getDouble(f)) - 1) / 2.0;
        }
    }
}
