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

import static java.lang.Math.sqrt;

/**
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public class FeatureSimilarities
{
    public static FeatureVectorSimilarity vectorCosine(FastUserFeatureData<?,?,Double> userFeatData, FastItemFeatureData<?, ?, Double> itemFeatData, boolean dense)
    {
        return new FeatureVectorSimilarity(userFeatData, itemFeatData, dense)
        {
            @Override
            protected double sim(double product, double norm2A, double norm2B)
            {
                return product / sqrt(norm2A*norm2B);
            }
        };
    }

    public static FeatureVectorSimilarity vectorJaccard(FastUserFeatureData<?,?,Double> userFeatData, FastItemFeatureData<?, ?, Double> itemFeatData, boolean dense)
    {
        return new FeatureVectorSimilarity(userFeatData, itemFeatData, dense)
        {
            @Override
            protected double sim(double product, double norm2A, double norm2B)
            {
                return product / (norm2A + norm2B - product);
            }
        };
    }
}
