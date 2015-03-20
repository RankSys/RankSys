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

import es.uam.eps.ir.ranksys.core.index.FeatureIndex;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface FastFeatureIndex<F> extends FeatureIndex<F> {

    @Override
    public default boolean containsFeature(F f) {
        return feature2fidx(f) >= 0;
    }

    @Override
    public default Stream<F> getAllFeatures() {
        return IntStream.range(0, numFeatures()).mapToObj(fidx -> fidx2feature(fidx));
    }

    public int feature2fidx(F f);

    public F fidx2feature(int fidx);

}
