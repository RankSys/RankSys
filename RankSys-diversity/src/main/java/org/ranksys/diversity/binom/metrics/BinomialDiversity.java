/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.diversity.binom.metrics;

import org.ranksys.core.feature.FeatureData;
import org.ranksys.diversity.binom.BinomialModel;
import org.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * Binomial diversity metric.
 *
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, redundancy and size-awareness in genre diversity for Recommender Systems. RecSys 2014.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class BinomialDiversity<U, I, F> extends BinomialMetric<U, I, F> {

    /**
     * Constructor.
     *
     * @param binomialModel binomial diversity model
     * @param featureData feature data
     * @param cutoff maximum length of the recommendation list to be evaluated
     * @param relModel relevance model
     */
    public BinomialDiversity(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        super(binomialModel, featureData, cutoff, relModel);
    }

    /**
     * Returns the value of the binomial diversity metric for a given count of 
     * features from a recommendation list.
     *
     * @param ubm user binomial model
     * @param count count map of each feature in the recommendation
     * @param nrel number of relevant documents in the recommendation
     * @param nret length of the recommendation
     * @return value of the binomial diversity
     */
    @Override
    protected double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel ubm, Object2IntMap<F> count, int nrel, int nret) {
        return BinomialNonRedundancy.nonRedundancy(ubm, count, nrel) * BinomialCoverage.coverage(ubm, count, nret);
    }
}
