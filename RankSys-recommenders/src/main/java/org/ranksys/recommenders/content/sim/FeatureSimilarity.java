/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.sim;

import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.core.feature.user.fast.FastUserFeatureData;
import org.ranksys.recommenders.nn.sim.Similarity;

/**
 * Similarity based on features.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public abstract class FeatureSimilarity implements Similarity
{
    /**
     * Item-feature data
     */
    protected final FastItemFeatureData<?,?,Double> itemFeatData;
    /**
     * User-feature data
     */
    protected final FastUserFeatureData<?,?,Double> userFeatData;

    /**
     * Constructor for item to item similarities.
     * @param itemFeatData item feature data.
     */
    public FeatureSimilarity(FastItemFeatureData<?,?,Double> itemFeatData)
    {
        this.itemFeatData = itemFeatData;
        this.userFeatData = null;
    }
    /**
     * Constructor for user to user similarities.
     * @param userFeatData user feature data.
     */
    public FeatureSimilarity(FastUserFeatureData<?,?,Double> userFeatData)
    {
        this.itemFeatData = null;
        this.userFeatData = userFeatData;
    }
    /**
     * Constructor for user to item similarities.
     * @param userFeatData user feature data.
     * @param itemFeatData item feature data.
     */
    public FeatureSimilarity(FastUserFeatureData<?,?,Double> userFeatData, FastItemFeatureData<?,?,Double> itemFeatData)
    {
        this.itemFeatData = itemFeatData;
        this.userFeatData = userFeatData;
    }
}
