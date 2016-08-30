/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.nn.item.sim;

import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.preference.fast.TransposedPreferenceData;
import org.ranksys.nn.sim.VectorCosineSimilarity;

/**
 * Vector cosine item similarity. See {@link VectorCosineSimilarity}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public class VectorCosineItemSimilarity<I> extends ItemSimilarity<I> {

    /**
     * Constructor.
     *
     * @param data preference
     * @param alpha asymmetry of the similarity, set to 0.5 to standard cosine.
     * @param dense true for array-based calculations, false to map-based
     */
    public VectorCosineItemSimilarity(FastPreferenceData<?, I> data, double alpha, boolean dense) {
        super(data, new VectorCosineSimilarity(new TransposedPreferenceData<>(data), alpha, dense));
    }
    
}
