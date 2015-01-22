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
package es.uam.eps.ir.ranksys.core.data;

import es.uam.eps.ir.ranksys.core.IdPref;
import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import es.uam.eps.ir.ranksys.core.index.UserIndex;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface RecommenderData<U, I, O> extends UserIndex<U>, ItemIndex<I> {

    public int numUsers(I i);

    public int numItems(U u);

    public int numPreferences();

    public Stream<IdPref<I, O>> getUserPreferences(U u);

    public Stream<IdPref<U, O>> getItemPreferences(I i);
}
