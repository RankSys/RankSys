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
package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.index.FeatureIndex;
import es.uam.eps.ir.ranksys.core.IdVar;
import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public interface FeatureData<I, F, V> extends ItemIndex<I>, FeatureIndex<F> {

    Stream<IdVar<I, V>> getFeatureItems(final F f);

    Stream<IdVar<F, V>> getItemFeatures(final I i);

    int numFeatures(I i);

    int numItems(F f);

}
