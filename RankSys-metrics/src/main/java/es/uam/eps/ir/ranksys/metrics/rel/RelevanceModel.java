/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.rel;

import org.ranksys.core.model.UserModel;
import java.util.stream.Stream;

/**
 * Relevance model: deciding when an item is relevant to a user and the
 * gain obtained for being recommended.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class RelevanceModel<U, I> extends UserModel<U> {

    /**
     * Full constructor: allows to specify whether to cache the user
     * relevance models and for which users.
     *
     * @param caching are the user relevance models cached?
     * @param users users whose relevance models are cached
     */
    public RelevanceModel(boolean caching, Stream<U> users) {
        super(caching, users);
    }

    /**
     * No caching constructor.
     */
    public RelevanceModel() {
        super();
    }

    /**
     * Caching constructor.
     *
     * @param users users whose relevance models are cached
     */
    public RelevanceModel(Stream<U> users) {
        super(users);
    }

    @Override
    protected abstract UserRelevanceModel<U, I> get(U user);

    @SuppressWarnings("unchecked")
    @Override
    public UserRelevanceModel<U, I> getModel(U user) {
        return (UserRelevanceModel<U, I>) super.getModel(user);
    }

    /**
     * User-specific relevance models.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     */
    public interface UserRelevanceModel<U, I> extends Model<U> {

        /**
         * Determines whether an item is relevant to the user or not
         *
         * @param item item to be judged as relevant
         * @return true if the item is relevant, false otherwise
         */
        public boolean isRelevant(I item);

        /**
         * Gain obtained by recommending the item. Should be typically
         * positive if the item is relevant, and 0 otherwise.
         *
         * @param item item whose gain is calculated
         * @return numerical gain
         */
        public double gain(I item);
    }
}
