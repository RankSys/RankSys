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
package es.uam.eps.ir.ranksys.fast.feature;

import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.IdxVar;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface FastFeatureData<I, F, V> extends FeatureData<I, F, V>, FastItemIndex<I>, FastFeatureIndex<F> {

    Stream<IdxVar<V>> getIidxFeatures(final int iidx);

    Stream<IdxVar<V>> getFidxItems(final int fidx);

    int numItems(int fidx);

    int numFeatures(int iidx);

    IntStream getIidxWithFeatures();
    
    IntStream getFidxWithItems();
}
