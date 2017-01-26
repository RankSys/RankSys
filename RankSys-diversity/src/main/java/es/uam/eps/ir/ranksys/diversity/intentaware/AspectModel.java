/*
 * Copyright (C) 2015 RankSys (http://ranksys.org)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.core.model.UserModel;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Aspect model for the intent-aware diversification framework. User intents
 * are taken from the intent model.
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 */
public abstract class AspectModel<U, I, F> extends UserModel<U> {

    /**
     * Intent model
     */
    protected IntentModel<U, I, F> intentModel;

    /**
     * Constructor taking the intent model
     *
     * @param intentModel intent model
     */
    public AspectModel(IntentModel<U, I, F> intentModel) {
        this.intentModel = intentModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserAspectModel getModel(U user) {
        return (UserAspectModel) super.getModel(user);
    }

    /**
     * User aspect model for {@link AspectModel}.
     */
    public abstract class UserAspectModel implements IntentModel.UserIntentModel<U, I, F> {

        private final IntentModel.UserIntentModel<U, I, F> uim;

        /**
         * Constructor taking user intent model.
         *
         * @param user user
         */
        public UserAspectModel(U user) {
            this.uim = intentModel.getModel(user);
        }

        /**
         * Returns an item aspect model from a list of scored items.
         *
         * @param items list of items with scores
         * @return an item aspect model for the list scored items supplied
         */
        public abstract ItemAspectModel<I, F> getItemAspectModel(List<Tuple2od<I>> items);

        @Override
        public Set<F> getIntents() {
            return uim.getIntents();
        }

        @Override
        public Stream<F> getItemIntents(I i) {
            return uim.getItemIntents(i);
        }

        @Override
        public double pf_u(F f) {
            return uim.pf_u(f);
        }
    }

    /**
     * Item aspect model for {@link AspectModel}.
     *
     * @param <I> item type
     * @param <F> aspect type
     */
    public interface ItemAspectModel<I, F> {
        /**
         * Returns probability of an item given an aspect
         *
         * @param iv item-value pair
         * @param f  aspect
         * @return probability of an item given an aspect
         */
        public abstract double pi_f(Tuple2od<I> iv, F f);
    }
}
