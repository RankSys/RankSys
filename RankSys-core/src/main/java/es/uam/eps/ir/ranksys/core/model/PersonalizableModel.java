/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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

import es.uam.eps.ir.ranksys.core.util.lazy.Lazy;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class PersonalizableModel<U> {

    private final boolean caching;
    private final Lazy<Map<U, UserModel<U>>> lazyUserMap;

    public PersonalizableModel(boolean caching, Stream<U> users) {
        this.caching = caching;
        if (caching) {
            this.lazyUserMap = new Lazy<>(() -> users.parallel().collect(Collectors.toMap(u -> u, u -> get(u))));
        } else {
            this.lazyUserMap = null;
        }
    }

    public boolean isCaching() {
        return caching;
    }

    public Set<U> getModeledUsers() {
        return lazyUserMap.get().keySet();
    }

    protected abstract UserModel<U> get(U u);

    public UserModel<U> getUserModel(U u) {
        if (caching) {
            return lazyUserMap.get().get(u);
        } else {
            return get(u);
        }
    }

    public interface UserModel<U> {

    }
}
