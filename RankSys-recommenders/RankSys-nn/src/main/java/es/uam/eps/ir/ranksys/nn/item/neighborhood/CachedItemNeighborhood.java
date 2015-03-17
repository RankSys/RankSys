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
package es.uam.eps.ir.ranksys.nn.item.neighborhood;

import es.uam.eps.ir.ranksys.nn.neighborhood.CachedNeighborhood;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class CachedItemNeighborhood<I> extends ItemNeighborhood<I> {

    public CachedItemNeighborhood(ItemNeighborhood<I> neighborhood) {
        super(neighborhood, new CachedNeighborhood(neighborhood.numItems(), neighborhood));
    }
}
