/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.itemnovelty;

import es.uam.eps.ir.ranksys.core.model.UserModel;
import java.util.stream.Stream;

/**
 * Item novelty model.
 * 
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 * 
 * S. Vargas and P. Castells. Rank and relevance in novelty and diversity for
 * Recommender Systems. RecSys 2011.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class ItemNovelty<U, I> extends UserModel<U> {

    /**
     * Constructor.
     *
     * @param caching are the user item novelty models cached?
     * @param users users whose item novelty models are cached
     */
    public ItemNovelty(boolean caching, Stream<U> users) {
        super(caching, users);
    }

    /**
     * Constructor in which no caching is done.
     */
    public ItemNovelty() {
        super();
    }

    /**
     * Constructor in which caching is done.
     *
     * @param users users whose item novelty models are cached
     */
    public ItemNovelty(Stream<U> users) {
        super(users);
    }

    @Override
    protected abstract UserItemNoveltyModel<U, I> get(U u);

    @SuppressWarnings("unchecked")
    @Override
    public UserItemNoveltyModel<U, I> getModel(U u) {
        return (UserItemNoveltyModel<U, I>) super.getModel(u);
    }

    /**
     * Item novelty model for a user.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     */
    public interface UserItemNoveltyModel<U, I> extends Model<U> {

        /**
         * Returns the novelty of an item
         *
         * @param i item
         * @return novelty of the item
         */
        public double novelty(I i);
    }
}
