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
 * Index for a set of items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface ItemIndex<I> {

    /**
     * Checks whether the index contains an item.
     *
     * @param i item
     * @return true if the index contains the item, false otherwise
     */
    public boolean containsItem(I i);
    
    /**
     * Counts the number of indexed items.
     *
     * @return the total number of item
     */
    public int numItems();
    
    /**
     * Retrieves a stream of the indexed items.
     *
     * @return a stream of all the items
     */
    public Stream<I> getAllItems();
}
