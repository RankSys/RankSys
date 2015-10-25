/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.VectorJaccardSimilarity;

/**
 * Vector Jaccard item similarity. See {@link VectorJaccardSimilarity}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public class VectorJaccardItemSimilarity<I> extends ItemSimilarity<I> {

    /**
     * Constructor.
     *
     * @param data preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public VectorJaccardItemSimilarity(FastPreferenceData<?, I> data, boolean dense) {
        super(data, new VectorJaccardSimilarity(new TransposedPreferenceData<>(data), dense));
    }
    
}
