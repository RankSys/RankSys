/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.user.sim;

import org.ranksys.core.feature.user.fast.FastUserFeatureData;
import org.ranksys.recommenders.content.sim.FeatureSimilarities;

/**
 * User feature similarity examples.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public class UserFeatureSimilarities
{
    public static <U> UserFeatureSimilarity<U> setCosine(FastUserFeatureData<U,?,Double> userFeats, boolean dense, double alpha) {
        return new UserFeatureSimilarity<>(userFeats, FeatureSimilarities.setCosine(userFeats, null, dense, alpha));
    }

    public static <U> UserFeatureSimilarity<U> vectorCosine(FastUserFeatureData<U,?,Double> userFeats, boolean dense) {
        return new UserFeatureSimilarity<>(userFeats, FeatureSimilarities.vectorCosine(userFeats, null, dense));
    }

    public static <U> UserFeatureSimilarity<U> setJaccard(FastUserFeatureData<U,?,Double> userFeats, boolean dense) {
        return new UserFeatureSimilarity<>(userFeats, FeatureSimilarities.setJaccard(userFeats, null, dense));
    }

    public static <U> UserFeatureSimilarity<U> vectorJaccard(FastUserFeatureData<U,?,Double> userFeats, boolean dense) {
        return new UserFeatureSimilarity<>(userFeats, FeatureSimilarities.vectorJaccard(userFeats, null, dense));
    }

    public static <U> UserFeatureSimilarity<U> logLikelihood(FastUserFeatureData<U,?,Double> userFeats, boolean dense) {
        return new UserFeatureSimilarity<>(userFeats, FeatureSimilarities.logLikelihood(userFeats, null, dense));
    }
}