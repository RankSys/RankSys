/*
 * Copyright (C) 2015 RankSys (http://ranksys.org)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.List;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Scores-based aspect model. User intents probabilities are taken from the input intent model, item probabilities are proportional to scores they obtained.
 *
 * S. Vargas, P. Castells and D. Vallet. Intent-oriented diversity in Recommender Systems. SIGIR 2011.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 */
public class ScoresAspectModel<U, I, F> extends AspectModel<U, I, F> {

    /**
     * Constructor taking an intent model and scores data.
     *
     * @param intentModel intent model
     */
    public ScoresAspectModel(IntentModel<U, I, F> intentModel) {
        super(intentModel);
    }

    @Override
    protected UserAspectModel get(U u) {
        return new ScoresUserAspectModel(u);
    }

    /**
     * User aspect model for {@link ScoresAspectModel}.
     */
    public class ScoresUserAspectModel extends UserAspectModel {

        /**
         * Constructor.
         *
         * @param user user
         */
        public ScoresUserAspectModel(U user) {
            super(user);
        }

        @Override
        public ItemAspectModel<I, F> getItemAspectModel(List<Tuple2od<I>> items) {
            Object2DoubleOpenHashMap<F> probNorm = new Object2DoubleOpenHashMap<>();
            items.forEach(iv -> {
                getItemIntents(iv.v1).forEach(f -> {
                    probNorm.addTo(f, iv.v2);
                });
            });

            return (iv, f) -> iv.v2 / probNorm.getDouble(f);
        }
    }
}
