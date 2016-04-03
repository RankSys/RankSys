/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.distance;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;

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

    /**
     * Returns a function that returns the feature-based similarity to the
     * features of an item.
     *
     * @param features1 stream of features of an item
     * @return function that returns the feature-based similarity to the
     * features of an item
     */
    @Override
    public ToDoubleFunction<Stream<Tuple2<F, Double>>> dist(Stream<Tuple2<F, Double>> features1) {
        Object2DoubleMap<F> auxMap = new Object2DoubleOpenHashMap<>();
        auxMap.defaultReturnValue(0.0);
        DoubleAdder norm1 = new DoubleAdder();

        features1.forEach(fv -> {
            auxMap.put(fv.v1, fv.v2);
            norm1.add(fv.v2 * fv.v2);
        });

        if (norm1.doubleValue() == 0) {
            return features2 -> Double.NaN;
        }

        return features2 -> {
            DoubleAdder prod = new DoubleAdder();
            DoubleAdder norm2 = new DoubleAdder();
            features2.forEach(fv -> {
                prod.add(fv.v2 * auxMap.getDouble(fv.v1));
                norm2.add(fv.v2 * fv.v2);
            });

            if (norm2.doubleValue() == 0) {
                return Double.NaN;
            }

            return dist(prod.doubleValue(), norm1.doubleValue(), norm2.doubleValue());
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
