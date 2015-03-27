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
package es.uam.eps.ir.ranksys.novelty.inverted.neighborhood;

import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class InvertedUserNeighborhood<U> extends UserNeighborhood<U> {

    public InvertedUserNeighborhood(UserNeighborhood<U> neighborhood, Predicate<U> filter) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numUsers(), neighborhood, uidx -> filter.test(neighborhood.uidx2user(uidx))));
    }
    
    public InvertedUserNeighborhood(UserNeighborhood<U> neighborhood, IntPredicate filter) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numUsers(), neighborhood, filter));
    }
}
