/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        if (caching) {
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
