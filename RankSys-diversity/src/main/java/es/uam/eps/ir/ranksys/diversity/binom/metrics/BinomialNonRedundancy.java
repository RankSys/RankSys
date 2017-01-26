/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.binom.metrics;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * Binomial redundancy metric.
 * 
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, redundancy
 * and size-awareness in genre diversity for Recommender Systems. RecSys 2014.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class BinomialNonRedundancy<U, I, F> extends BinomialMetric<U, I, F> {

    /**
     * Constructor.
     *
     * @param binomialModel binomial diversity model
     * @param featureData feature data
     * @param cutoff maximum length of the recommendation list to be evaluated
     * @param relModel relevance model
     */
    public BinomialNonRedundancy(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        super(binomialModel, featureData, cutoff, relModel);
    }

    @Override
    protected double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, Object2IntMap<F> count, int nrel, int nret) {
        return nonRedundancy(prob, count, nrel);
    }

    /**
     * Returns the value of the binomial redundancy metric for a given count of
     * features from a recommendation list.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param <F> type of the features
     * @param ubm user binomial model
     * @param count count map of each feature in a recommendation
     * @param nrel number of relevant items in the recommendation
     * @return value of the binomial redundancy
     */
    protected static <U, I, F> double nonRedundancy(BinomialModel<U, I, F>.UserBinomialModel ubm, Object2IntMap<F> count, int nrel) {
        if (nrel == 0 || count.isEmpty()) {
            return 0.0;
        }
        
        double nonRedundancy = ubm.getFeatures().stream().
                filter(count::containsKey).
                mapToDouble(f -> ubm.patience(count.getInt(f), f, nrel)).
                reduce(1.0, (p, q) -> p * q);
        nonRedundancy = Math.pow(nonRedundancy, 1.0 / (double) count.size());

        return nonRedundancy;
    }
}
