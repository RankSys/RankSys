/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.item.sim;

import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.recommenders.content.sim.FeatureSimilarities;

/**
 * Item feature similarity examples.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public class ItemFeatureSimilarities
{
    public static <I> ItemFeatureSimilarity<I> setCosine(FastItemFeatureData<I,?,Double> itemFeats, boolean dense, double alpha) {
        return new ItemFeatureSimilarity<>(itemFeats, FeatureSimilarities.setCosine(null, itemFeats, dense, alpha));
    }

    public static <I> ItemFeatureSimilarity<I> vectorCosine(FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new ItemFeatureSimilarity<>(itemFeats, FeatureSimilarities.vectorCosine(null, itemFeats, dense));
    }

    public static <I> ItemFeatureSimilarity<I> setJaccard(FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new ItemFeatureSimilarity<>(itemFeats, FeatureSimilarities.setJaccard(null, itemFeats, dense));
    }

    public static <I> ItemFeatureSimilarity<I> vectorJaccard(FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new ItemFeatureSimilarity<>(itemFeats, FeatureSimilarities.vectorJaccard(null, itemFeats, dense));
    }

    public static <I> ItemFeatureSimilarity<I> logLikelihood(FastItemFeatureData<I,?,Double> itemFeats, boolean dense) {
        return new ItemFeatureSimilarity<>(itemFeats, FeatureSimilarities.logLikelihood(null, itemFeats, dense));
    }
}
