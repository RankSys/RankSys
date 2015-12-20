/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.core.index.FeatureIndex;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast version of FeatureIndex, where features are internally represented with 
 * numerical indices from 0 (inclusive) to the number of indexed features
 * (exclusive).
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <F> type of the features
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

    /**
     * Gets all the indices of the features.
     *
     * @return a stream of indexes of features
     */
    public default IntStream getAllFidx() {
        return IntStream.range(0, numFeatures());
    }
    
    /**
     * Returns the index assigned to the feature.
     *
     * @param f feature
     * @return the index of the feature, or -1 if the feature does not exist
     */
    public int feature2fidx(F f);

    /**
     * Returns the feature represented with the index.
     *
     * @param fidx feature index
     * @return the feature whose index is fidx
     */
    public F fidx2feature(int fidx);
}
