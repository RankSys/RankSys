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

import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SimpleFastFeatureIndex<F> implements FastFeatureIndex<F> {

    private final IdxIndex<F> fMap;

    public SimpleFastFeatureIndex() {
        this.fMap = new IdxIndex<>();
    }

    @Override
    public boolean containsFeature(F f) {
        return fMap.containsId(f);
    }

    @Override
    public int numFeatures() {
        return fMap.size();
    }

    @Override
    public Stream<F> getAllFeatures() {
        return StreamSupport.stream(fMap.getIds().spliterator(), false);
    }

    @Override
    public int feature2fidx(F f) {
        return fMap.get(f);
    }

    @Override
    public F fidx2feature(int fidx) {
        return fMap.get(fidx);
    }

    public int add(F f) {
        return fMap.add(f);
    }

}
