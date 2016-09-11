/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.nn.sim;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.apache.mahout.math.stats.LogLikelihood.logLikelihoodRatio;
import org.ranksys.core.preference.fast.FastPreferenceData;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class Similarities {

    public static SetSimilarity setCosine(FastPreferenceData<?, ?> preferences, boolean dense, double alpha) {
        return new SetSimilarity(preferences, dense) {
            @Override
            protected double sim(int intersectionSize, int na, int nb) {
                return intersectionSize / (pow(na, alpha) * pow(nb, 1.0 - alpha));
            }
        };
    }

    public static SetSimilarity setJaccard(FastPreferenceData<?, ?> preferences, boolean dense) {
        return new SetSimilarity(preferences, dense) {
            @Override
            protected double sim(int intersectionSize, int na, int nb) {
                return intersectionSize / (double) (na + nb - intersectionSize);
            }
        };
    }

    public static VectorSimilarity vectorCosine(FastPreferenceData<?, ?> preferences, boolean dense) {
        return new VectorSimilarity(preferences, dense) {
            @Override
            protected double sim(double product, double norm2A, double norm2B) {
                return product / sqrt(norm2A * norm2B);
            }
        };
    }

    public static VectorSimilarity vectorJaccard(FastPreferenceData<?, ?> preferences, boolean dense) {
        return new VectorSimilarity(preferences, dense) {
            @Override
            protected double sim(double product, double norm2A, double norm2B) {
                return product / (norm2A + norm2B - product);
            }
        };
    }

    public static SetSimilarity logLikelihood(FastPreferenceData<?, ?> preferences, boolean dense) {
        return new SetSimilarity(preferences, dense) {
            @Override
            protected double sim(int intersectionSize, int na, int nb) {
                double logLikelihood = logLikelihoodRatio((long) intersectionSize,
                        (long) (nb - intersectionSize),
                        (long) (na - intersectionSize),
                        (long) (data.numItems() - na - nb + intersectionSize));
                return 1.0 - 1.0 / (1.0 + logLikelihood);
            }
        };
    }
}
