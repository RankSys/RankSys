/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.nn.sim;

import org.ranksys.core.preference.fast.FastPreferenceData;
import static java.lang.Math.pow;

/**
 * Vector cosine similarity. As in Cremonesi's paper. Can be asymmetric if alpha != 0.5.
 *
 * sim(v, w) = v * w / ((v * v)^alpha (w * w)^(1 - alpha))
 * <br>
 * F. Aiolli. Efficient Top-N Recommendation for Very Large Scale Binary Rated Datasets. RecSys 2013.
 * <br>
 * P. Cremonesi, Y. Koren, and R. Turrin. Performance of recommender algorithms on top-N recommendation tasks. RecSys 2010.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class VectorCosineSimilarity extends VectorSimilarity {

    private final double alpha;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param alpha asymmetry of the similarity, set to 0.5 for symmetry
     * @param dense true for array-based calculations, false to map-based
     */
    public VectorCosineSimilarity(FastPreferenceData<?, ?> data, double alpha, boolean dense) {
        super(data, dense);
        this.alpha = alpha;
    }

    @Override
    protected double sim(double product, double norm2A, double norm2B) {
        return product / (pow(norm2A, alpha) * pow(norm2B, 1.0 - alpha));
    }

}
