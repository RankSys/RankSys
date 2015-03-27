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
package es.uam.eps.ir.ranksys.nn.neighborhood;

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import java.util.stream.Stream;

/**
 * Generic fast neighborhood. Implementing classes of this interface are under the
 * hood of user and item neighborhoods.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface Neighborhood {
    
    /**
     * Returns the neighborhood of a user/index.
     *
     * @param idx user/index whose neighborhood is calculated
     * @return stream of user/item-similarity pairs.
     */
    public Stream<IdxDouble> getNeighbors(int idx);
}
