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
package es.uam.eps.ir.ranksys.novdiv.distance;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

/**
 * Feature-based item distance model that considers the features of items as
 * vectors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 */
public abstract class VectorFeatureItemDistanceModel<I, F> extends FeatureItemDistanceModel<I, F, Double> {

    /**
     * Constructor.
     *
     * @param featureData feature data
     */
    public VectorFeatureItemDistanceModel(FeatureData<I, F, Double> featureData) {
        super(featureData);
    }

    @Override
    public ToDoubleFunction<Stream<IdObject<F, Double>>> dist(Stream<IdObject<F, Double>> features1) {
        Object2DoubleMap<F> auxMap = new Object2DoubleOpenHashMap<>();
        auxMap.defaultReturnValue(0.0);
        double[] norm1 = {0.0};

        features1.forEach(fv -> {
            auxMap.put(fv.id, fv.v);
            norm1[0] += fv.v * fv.v;
        });

        if (norm1[0] == 0) {
            return features2 -> Double.NaN;
        }

        return features2 -> {
            double[] prod = {0.0};
            double[] norm2 = {0.0};
            features2.forEach(fv -> {
                prod[0] += fv.v * auxMap.getDouble(fv.id);
                norm2[0] += fv.v * fv.v;
            });

            if (norm2[0] == 0) {
                return Double.NaN;
            }

            return dist(prod[0], norm1[0], norm2[0]);
        };
    }

    /**
     * Distance as a function of the inner product between feature vectors
     * and the square of the norms of these vectors.
     *
     * @param prod inner product of two vectors
     * @param norm2A square norm of the first vector
     * @param norm2B square norm of the second vector
     * @return distance value
     */
    protected abstract double dist(double prod, double norm2A, double norm2B);

}
