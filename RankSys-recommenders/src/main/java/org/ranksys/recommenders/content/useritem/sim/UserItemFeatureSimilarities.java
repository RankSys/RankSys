/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.useritem.sim;

import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.core.feature.user.fast.FastUserFeatureData;
import org.ranksys.recommenders.content.item.sim.ItemFeatureSimilarity;
import org.ranksys.recommenders.content.sim.FeatureSimilarities;

/**
 * User-item feature similarity examples.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public class UserItemFeatureSimilarities
{
    public static <U,I> UserItemFeatureSimilarity<U,I> setCosine(FastUserFeatureData<U,?,Double> userFeats, FastItemFeatureData<I,?,Double> itemFeats, boolean dense, double alpha) {
        return new UserItemFeatureSimilarity<>(userFeats, itemFeats, FeatureSimilarities.setCosine(userFeats, itemFeats, dense, alpha));
    }

    public static <U,I> UserItemFeatureSimilarity<U,I> vectorCosine(FastUserFeatureData<U,?,Double> userFeats,FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new UserItemFeatureSimilarity<>(userFeats, itemFeats, FeatureSimilarities.vectorCosine(userFeats, itemFeats, dense));
    }

    public static <U,I> UserItemFeatureSimilarity<U,I> setJaccard(FastUserFeatureData<U,?,Double> userFeats,FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new UserItemFeatureSimilarity<>(userFeats, itemFeats, FeatureSimilarities.setJaccard(userFeats, itemFeats, dense));
    }

    public static <U,I> UserItemFeatureSimilarity<U,I> vectorJaccard(FastUserFeatureData<U,?,Double> userFeats,FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new UserItemFeatureSimilarity<>(userFeats, itemFeats, FeatureSimilarities.vectorJaccard(userFeats, itemFeats, dense));
    }

    public static <U,I> UserItemFeatureSimilarity<U,I> logLikelihood(FastUserFeatureData<U,?,Double> userFeats,FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new UserItemFeatureSimilarity<>(userFeats, itemFeats, FeatureSimilarities.logLikelihood(userFeats, itemFeats, dense));
    }
}
