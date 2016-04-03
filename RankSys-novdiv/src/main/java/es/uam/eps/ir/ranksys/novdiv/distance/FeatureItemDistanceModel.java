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
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;

/**
 * Feature-based item distance model.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information between item-feature pairs
 */
public abstract class FeatureItemDistanceModel<I, F, V> implements ItemDistanceModel<I> {

    private final FeatureData<I, F, V> featureData;

    /**
     * Constructor.
     *
     * @param featureData feature data
     */
    public FeatureItemDistanceModel(FeatureData<I, F, V> featureData) {
        this.featureData = featureData;
    }

    /**
     * Returns a function that return the distance to the input item.
     *
     * @param i item
     * @return function that return the distance to the input item
     */
    @Override
    public ToDoubleFunction<I> dist(I i) {
        Stream<Tuple2<F, V>> features1 = featureData.getItemFeatures(i);
        ToDoubleFunction<Stream<Tuple2<F, V>>> iDist = dist(features1);
        return j -> {
            return iDist.applyAsDouble(featureData.getItemFeatures(j));
        };
    }

    /**
     * Returns a function that returns the feature-based similarity to the
     * features of an item.
     *
     * @param features1 stream of features of an item
     * @return function that returns the feature-based similarity to the
     * features of an item
     */
    protected abstract ToDoubleFunction<Stream<Tuple2<F, V>>> dist(Stream<Tuple2<F, V>> features1);
}
