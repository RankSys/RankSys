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
import org.ranksys.core.preference.fast.FastPreferenceData;

import static java.lang.Math.sqrt;
import static org.apache.mahout.math.stats.LogLikelihood.logLikelihoodRatio;

/**
 * Examples of feature similarity.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public class FeatureSimilarities
{
    public static FeatureSetSimilarity setCosine(FastUserFeatureData<?,?,Double> userFeatData, FastItemFeatureData<?,?,Double> itemFeatData, boolean dense, double alpha)
    {
        return new FeatureSetSimilarity(userFeatData, itemFeatData, dense)
        {
            @Override
            protected double sim(int intersectionSize, int na, int nb)
            {
                return intersectionSize / (Math.pow(na, alpha) * Math.pow(nb, 1.0 - alpha));
            }
        };
    }

    public static FeatureSetSimilarity setJaccard(FastUserFeatureData<?,?, Double> userFeatData, FastItemFeatureData<?,?,Double> itemFeatData, boolean dense)
    {
        return new FeatureSetSimilarity(userFeatData, itemFeatData, dense)
        {
            @Override
            protected double sim(int intersectionSize, int na, int nb)
            {
                return intersectionSize / (double) (na+nb-intersectionSize);
            }
        };
    }

    public static FeatureVectorSimilarity vectorCosine(FastUserFeatureData<?,?,Double> userFeatData, FastItemFeatureData<?, ?, Double> itemFeatData, boolean dense)
    {
        return new FeatureVectorSimilarity(userFeatData, itemFeatData, dense)
        {
            @Override
            protected double sim(double product, double norm2A, double norm2B)
            {
                return product /sqrt(norm2A*norm2B);
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

    public static FeatureSetSimilarity logLikelihood(FastUserFeatureData<?,?,Double> userFeatData, FastItemFeatureData<?,?,Double> itemFeatData, boolean dense)
    {
        return new FeatureSetSimilarity(userFeatData, itemFeatData, dense)
        {
            @Override
            protected double sim(int intersectionSize, int na, int nb)
            {
                int numFeats;
                if(userFeatData == null) numFeats = itemFeatData.numFeatures();
                else numFeats = userFeatData.numFeatures();
                double logLikelihood = logLikelihoodRatio( intersectionSize,
                        (nb-intersectionSize),
                        (na-intersectionSize),
                        (numFeats - na - nb + intersectionSize));
                return 1.0 - 1.0/(1.0 + logLikelihood);
            }
        };
    }
}
