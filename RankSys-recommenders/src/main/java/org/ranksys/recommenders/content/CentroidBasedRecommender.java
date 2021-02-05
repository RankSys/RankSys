/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;

import org.ranksys.recommenders.content.useritem.sim.UserItemFeatureSimilarity;
import org.ranksys.recommenders.fast.FastRankingRecommender;

/**
 * Content-based approach based on finding the similarity between a user profile and
 * an item.
 *
 * G. Adomavicius, A. Tuzhilin. Toward the Next Generation of RecommenderSystems :
 * A Survey of the State-of-the-Art and Possible Extensions. IEEE TKDE 2005.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class CentroidBasedRecommender<U,I> extends FastRankingRecommender<U, I>
{
    /**
     * User-item feature similarity.
     */
    private final UserItemFeatureSimilarity<U,I> similarity;

    /**
     * Constructor.
     *
     * @param similarity user-item feature-based similarity.
     */
    public CentroidBasedRecommender(UserItemFeatureSimilarity<U,I> similarity)
    {
        super(similarity, similarity);
        this.similarity = similarity;
    }

    @Override
    public Int2DoubleMap getScoresMap(int uidx)
    {
        Int2DoubleMap scores = new Int2DoubleOpenHashMap();
        similarity.similarItems(uidx).forEach(sim -> scores.put(sim.v1, sim.v2));
        return scores;
    }
}
