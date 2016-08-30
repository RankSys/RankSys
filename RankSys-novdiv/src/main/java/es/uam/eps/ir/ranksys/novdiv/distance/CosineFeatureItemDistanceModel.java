/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.distance;

import org.ranksys.core.feature.FeatureData;

/**
 * Vector cosine item distance model.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class CosineFeatureItemDistanceModel<I, F> extends VectorFeatureItemDistanceModel<I, F> {

    /**
     * Constructor.
     *
     * @param featureData feature data
     */
    public CosineFeatureItemDistanceModel(FeatureData<I, F, Double> featureData) {
        super(featureData);
    }

    @Override
    protected double dist(double prod, double norm2A, double norm2B) {
        return 1 - prod / Math.sqrt(norm2A * norm2B);
    }

}
