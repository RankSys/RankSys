/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.core;

import java.util.stream.Stream;

/**
 * Index for a set of users.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface UserIndex<U> {

    /**
     * Checks whether the index contains a user.
     *
     * @param u user
     * @return true if the index contains the user, false otherwise
     */
    public boolean containsUser(U u);

    /**
     * Counts the number of indexed users.
     *
     * @return the total number of users
     */
    public int numUsers();

    /**
     * Retrieves a stream of the indexed users.
     *
     * @return a stream of all the users
     */
    public Stream<U> getAllUsers();

}
