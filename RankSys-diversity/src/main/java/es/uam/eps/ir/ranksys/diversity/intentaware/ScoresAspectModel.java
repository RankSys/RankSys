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
 * Scores-based aspect model. User intents probabilities are taken from the
 * input intent model, item probabilities are proportional to scores they
 * obtained.
 *
 * S. Vargas, P. Castells and D. Vallet. Intent-oriented diversity in
 * Recommender Systems. SIGIR 2011.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 */
public class ScoresAspectModel<U, I, F> extends AspectModel<U, I, F> {

    private final PreferenceData<U, I> scoresData;

    /**
     * Constructor taking an intent model and scores data.
     *
     * @param intentModel intent model
     * @param scoresData scores data
     */
    public ScoresAspectModel(IntentModel<U, I, F> intentModel, PreferenceData<U, I> scoresData) {
        super(intentModel);
        this.scoresData = scoresData;
    }

    @Override
    protected UserAspectModel get(U u) {
        return new ScoresUserAspectModel(u);
    }

    /**
     * User aspect model for {@link ScoresAspectModel}.
     */
    public class ScoresUserAspectModel extends UserAspectModel {

        private final Object2DoubleOpenHashMap<F> probNorm;
        private final Object2DoubleOpenHashMap<I> scores;

        /**
         * Constructor.
         *
         * @param user user
         */
        public ScoresUserAspectModel(U user) {
            super(user);
            this.probNorm = new Object2DoubleOpenHashMap<>();
            this.scores = new Object2DoubleOpenHashMap<>();
            scoresData.getUserPreferences(user).forEach(iv -> {
                getItemIntents(iv.v1).sequential().forEach(f -> {
                    probNorm.addTo(f, iv.v2);
                });
                scores.put(iv.v1, iv.v2);
            });
        }

        @Override
        public double pi_f(I i, F f) {
            return scores.get(i) / probNorm.getDouble(f);
        }
    }
}
