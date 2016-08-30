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

/**
 * Set Jaccard similarity.
 * 
 * sim(A, B) = |A n B| / |A u B|
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SetJaccardSimilarity extends SetSimilarity {

    /**
     * Constructor.
     *
     * @param data preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public SetJaccardSimilarity(FastPreferenceData<?, ?> data, boolean dense) {
        super(data, dense);
    }

    @Override
    protected double sim(int intersectionSize, int nA, int nB) {
        return intersectionSize / (double) (nA + nB - intersectionSize);
    }

}
