/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index;

import java.util.stream.Stream;

/**
 * Index for a set of features.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <F> type of the features
 */
public interface FeatureIndex<F> {

    /**
     * Checks whether the index contains a feature.
     *
     * @param f feature
     * @return true if the index contains the feature, false otherwise
     */
    public boolean containsFeature(F f);

    /**
     * Counts the number of indexed features.
     *
     * @return the total number of features
     */
    public int numFeatures();

    /**
     * Retrieves a stream of the indexed features.
     *
     * @return a stream of all the features
     */
    public Stream<F> getAllFeatures();

}
