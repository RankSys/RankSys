/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.nn.sim;

import org.ranksys.fast.preference.FastPreferenceData;

/**
 * Vector Jaccard similarity.
 * 
 * sim(v, w) = v * w / (v * v + w * w - v * w)
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class VectorJaccardSimilarity extends VectorSimilarity {

    /**
     * Constructor.
     *
     * @param data preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public VectorJaccardSimilarity(FastPreferenceData<?, ?> data, boolean dense) {
        super(data, dense);
    }

    @Override
    protected double sim(double product, double norm2A, double norm2B) {
        return product / (norm2A + norm2B - product);
    }

}
