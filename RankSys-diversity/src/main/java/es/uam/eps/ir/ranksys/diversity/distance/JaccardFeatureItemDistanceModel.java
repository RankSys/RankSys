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
package es.uam.eps.ir.ranksys.diversity.distance;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class JaccardFeatureItemDistanceModel<I, F> extends FeatureItemDistanceModel<I, F, Double> {

    public JaccardFeatureItemDistanceModel(FeatureData<I, F, Double> featureData) {
        super(featureData);
    }

    @Override
    public double dist(Stream<IdValuePair<F, Double>> features1, Stream<IdValuePair<F, Double>> features2) {
        TObjectDoubleMap<F> auxMap = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);
        double[] comps = {0.0, 0.0, 0.0};

        features1.forEach(fv -> {
            auxMap.put(fv.id, fv.v);
            comps[0] += fv.v * fv.v;
        });
        
        if (comps[0] == 0) {
            return Double.NaN;
        }

        features2.forEach(fv -> {
            comps[1] += fv.v * fv.v;
            comps[2] += fv.v * auxMap.get(fv.id);
        });
        
        if (comps[1] == 0) {
            return Double.NaN;
        }
        
        return 1 - comps[2] / (comps[0] + comps[1] - comps[2]);
    }

}
