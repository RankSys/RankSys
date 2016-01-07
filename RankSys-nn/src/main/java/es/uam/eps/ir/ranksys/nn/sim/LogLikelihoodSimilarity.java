/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import static org.apache.mahout.math.stats.LogLikelihood.logLikelihoodRatio;

/**
 * Log-likelihood similarity.
 *
 * Adapted from Apache Mahout (0.8) org.apache.mahout.math.hadoop.similarity.cooccurrence.measure.LoglikelihoodSimilarity
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public class LogLikelihoodSimilarity extends SetSimilarity {

    public LogLikelihoodSimilarity(FastPreferenceData<?, ?> data, boolean dense) {
        super(data, dense);
    }

    @Override
    protected double sim(int intersectionSize, int na, int nb) {
        double logLikelihood = logLikelihoodRatio((long) intersectionSize,
                (long) (nb - intersectionSize),
                (long) (na - intersectionSize),
                (long) (data.numItems() - na - nb + intersectionSize));
        return 1.0 - 1.0 / (1.0 + logLikelihood);
    }

}
