/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.rel;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Relevance model in which there is full, a-priori knowledge of the relevance
 * of all the items in the collection.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class IdealRelevanceModel<U, I> extends RelevanceModel<U, I> {

    /**
     * Full constructor: allows to specify whether to cache the user
     * relevance models and for which users.
     *
     * @param caching are the user relevance models cached?
     * @param users users whose relevance models are cached
     */
    public IdealRelevanceModel(boolean caching, Stream<U> users) {
        super(caching, users);
    }

    /**
     * No caching constructor.
     */
    public IdealRelevanceModel() {
        super();
    }

    /**
     * Caching constructor.
     *
     * @param users users whose relevance models are cached
     */
    public IdealRelevanceModel(Stream<U> users) {
        super(users);
    }

    @Override
    protected abstract UserIdealRelevanceModel<U, I> get(U user);

    @Override
    public UserIdealRelevanceModel<U, I> getModel(U user) {
        return (UserIdealRelevanceModel<U, I>) super.getModel(user);
    }

    /**
     * User relevance model for IdealRelevanceModel
     *
     * @param <U> type of the users
     * @param <I> type of the item
     */
    public interface UserIdealRelevanceModel<U, I> extends UserRelevanceModel<U, I> {

        /**
         * Obtains all the items relevant to the user.
         *
         * @return set of items relevant to the user
         */
        public Set<I> getRelevantItems();

    }
}
