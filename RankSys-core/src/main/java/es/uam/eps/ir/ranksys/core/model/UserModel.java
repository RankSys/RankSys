/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.model;

import es.uam.eps.ir.ranksys.core.util.Lazy;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generic class for representing (and caching) user models.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the user
 */
public abstract class UserModel<U> {

    private final boolean caching;
    private final Lazy<Map<U, Model<U>>> lazyUserMap;

    /**
     * Constructor in which it can be specified whether to perform
     * caching or not and the target users.
     *
     * @param caching specify if user models are to be cached
     * @param users users whose models are cached
     */
    public UserModel(boolean caching, Stream<U> users) {
        this.caching = caching;
        if (caching) {
            this.lazyUserMap = new Lazy<>(() -> users.parallel().collect(Collectors.toMap(u -> u, u -> get(u))));
        } else {
            this.lazyUserMap = null;
        }
    }

    /**
     * Constructor which does not do any caching.
     *
     */
    public UserModel() {
        this(false, null);
    }

    /**
     * Constructor which lazily caches the models for the specified users.
     *
     * @param users set of users for which their results will be cached
     */
    public UserModel(Stream<U> users) {
        this(true, users);
    }

    /**
     * Constructor which lazily caches the models for the same users the model in the parameter does.
     *
     * @param model another model from which we see
     */
    public UserModel(UserModel<U> model) {
        this(model.caching, model.caching ? model.lazyUserMap.get().keySet().stream() : null);
    }

    /**
     * Start the caching of the users.
     */
    public void initialize() {
        if (caching) {
            lazyUserMap.get();
        }
    }

    /**
     * Calculates the user model for user u.
     *
     * @param u the user
     * @return a user model
     */
    protected abstract Model<U> get(U u);

    /**
     * Returns the user model for user u.
     *
     * @param u the user
     * @return a user model
     */
    public Model<U> getModel(U u) {
        if (caching && lazyUserMap.get().containsKey(u)) {
            return lazyUserMap.get().get(u);
        } else {
            return get(u);
        }
    }

    /**
     * User model
     *
     * @param <U> type of the user
     */
    public interface Model<U> {

    }
}
