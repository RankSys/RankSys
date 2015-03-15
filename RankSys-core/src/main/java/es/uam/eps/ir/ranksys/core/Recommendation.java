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

import java.util.List;

/**
 * A recommendation issued to a user.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Recommendation<U, I> {

    private final U user;
    private final List<IdDouble<I>> items;

    /**
     * Constructs the recommendation.
     *
     * @param user the user that receives the recommendation
     * @param items a list of item ID-score pairs sorted by descending score
     */
    public Recommendation(U user, List<IdDouble<I>> items) {
        this.user = user;
        this.items = items;
    }

    /**
     * Returns the user that receives the recommendation.
     * 
     * @return the ID of the user
     */
    public U getUser() {
        return user;
    }

    /**
     * Returns the list of item-score pairs.
     * 
     * @return a list of item ID-score pairs sorted by descending score
     */
    public List<IdDouble<I>> getItems() {
        return items;
    }
}
