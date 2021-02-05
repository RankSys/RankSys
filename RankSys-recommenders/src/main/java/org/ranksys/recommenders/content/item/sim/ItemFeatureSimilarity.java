/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.item.sim;

import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.recommenders.content.sim.FeatureSimilarity;
import org.ranksys.recommenders.nn.item.sim.ItemSimilarity;

/**
 * Item similarity. It wraps a generic fast feature similarity and a fast item index.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <I> type of the items
 */
public class ItemFeatureSimilarity<I> extends ItemSimilarity<I>
{
    /**
     * Constructor.
     *
     * @param iIndex fast item index
     * @param sim    generic fast similarity
     */
    public ItemFeatureSimilarity(FastItemIndex<I> iIndex, FeatureSimilarity sim)
    {
        super(iIndex, sim);
    }
}
