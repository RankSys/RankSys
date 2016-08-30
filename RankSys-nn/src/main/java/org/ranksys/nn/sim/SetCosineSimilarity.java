/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.nn.sim;

import org.ranksys.core.preference.fast.FastPreferenceData;
import static java.lang.Math.pow;

/**
 * Set cosine similarity. As in Aiolli's paper. Can be asymmetric if 
 * alpha != 0.5.
 * 
 * sim(A, B) = |A n B| / (|A|^alpha |B|^(1 - alpha))
 *
 * F. Aiolli. Efficient Top-N Recommendation for Very Large Scale Binary Rated
 * Datasets. RecSys 2013.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SetCosineSimilarity extends SetSimilarity {

    private final double alpha;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param alpha asymmetry of the similarity, set to 0.5 for standard cosine
     * @param dense true for array-based calculations, false to map-based
     */
    public SetCosineSimilarity(FastPreferenceData<?, ?> data, double alpha, boolean dense) {
        super(data, dense);
        this.alpha = alpha;
    }

    @Override
    protected double sim(int intersectionSize, int nA, int nB) {
        return intersectionSize / (pow(nA, alpha) * pow(nB, 1.0 - alpha));
    }
}
