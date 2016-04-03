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
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Abstract class for metrics using the binomial model.
 *
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, redundancy and size-awareness in genre diversity for Recommender Systems. RecSys 2014.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <F> type of the items
 * @param <I> type of the features
 */
public abstract class BinomialMetric<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final BinomialModel<U, I, F> binomialModel;
    private final FeatureData<I, F, ?> featureData;
    private final int cutoff;

    /**
     * relevance model
     */
    protected final RelevanceModel<U, I> relModel;

    /**
     * Constructor.
     *
     * @param binomialModel binomial diversity model
     * @param featureData feature data
     * @param cutoff maximum length of the recommendation list to be evaluated
     * @param relModel relevance model
     */
    public BinomialMetric(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        this.binomialModel = binomialModel;
        this.featureData = featureData;
        this.cutoff = cutoff;
        this.relModel = relModel;
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());
        BinomialModel<U, I, F>.UserBinomialModel prob = binomialModel.getModel(recommendation.getUser());

        Object2IntOpenHashMap<F> count = new Object2IntOpenHashMap<>();
        count.defaultReturnValue(0);

        int rank = 0;
        int nrel = 0;
        for (Tuple2od<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.v1)) {
                featureData.getItemFeatures(iv.v1).forEach(fv -> {
                    count.addTo(fv.v1, 1);
                });
                nrel++;
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        return getResultFromCount(prob, count, nrel, rank);
    }

    /**
     * Result of the metric based on the number of times each features appears in a recommendation list.
     *
     * @param prob user binomial model
     * @param count count map of each feature in a recommendation
     * @param nrel number of relevant items in the recommendation
     * @param nret length of the recommendation
     * @return value of the metric
     */
    protected abstract double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, Object2IntMap<F> count, int nrel, int nret);

}
