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
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.util.stream.Stream;

/**
 * Simple implementation of FastUserIndex backed by a bi-map IdxIndex
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public class SimpleFastUserIndex<U> implements FastUserIndex<U> {

    private final IdxIndex<U> uMap;

    /**
     * Constructor.
     *
     */
    public SimpleFastUserIndex() {
        this.uMap = new IdxIndex<>();
    }

    @Override
    public boolean containsUser(U u) {
        return uMap.containsId(u);
    }

    @Override
    public int numUsers() {
        return uMap.size();
    }

    @Override
    public Stream<U> getAllUsers() {
        return uMap.getIds();
    }

    @Override
    public int user2uidx(U u) {
        return uMap.get(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uMap.get(uidx);
    }

    /**
     * Add a new user to the index. If the user already exists, nothing is
     * done.
     *
     * @param u id of the user
     * @return index of the user
     */
    public int add(U u) {
        return uMap.add(u);
    }

}
