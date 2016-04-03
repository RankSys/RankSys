/* 
 * Copyright (C) 2015 RankSys (http://ranksys.org)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.diversity.prop.metrics;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Cumulative proportionality metric.
 * <br>
 * 
 * Dang, V., Croft, W. B. (2012). Diversity by Proportionality: An Election-based Approach to Search Result Diversification. In Proceedings of the 35th International ACM SIGIR Conference on Research and Development in Information Retrieval (pp. 65–74). New York, NY, USA: ACM. doi:10.1145/2348283.2348296
 *
 * @author Saúl Vargas (saul.vargas@mendeley.com)
 *
 * @param <U> type of user
 * @param <I> type of item
 * @param <F> type of feature
 */
public class CPR<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final BinomialModel<U, I, F> binomialModel;
    private final FeatureData<I, F, ?> featureData;
    private final int cutoff;
    private final RelevanceModel<U, I> relModel;

    /**
     * Constructor.
     *
     * @param binomialModel binomial model for features
     * @param featureData feature data
     * @param cutoff metric cutoff
     * @param relModel relevance model
     */
    public CPR(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        this.binomialModel = binomialModel;
        this.featureData = featureData;
        this.cutoff = cutoff;
        this.relModel = relModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());
        BinomialModel<U, I, F>.UserBinomialModel prob = binomialModel.getModel(recommendation.getUser());

        Object2IntOpenHashMap<F> count = new Object2IntOpenHashMap<>();
        count.defaultReturnValue(0);

        int rank = 0;
        int nr = 0;
        double cpr = 0.0;
        for (Tuple2od<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.v1)) {
                featureData.getItemFeatures(iv.v1).forEach(fv -> {
                    count.addTo(fv.v1, 1);
                });
            } else {
                nr++;
            }

            double[] disprop = {0.5 * nr * nr};
            double[] ideal = {0};
            if (relModel instanceof NoRelevanceModel) {
                ideal[0] = 0;
            } else {
                ideal[0] = 0.5 * (rank + 1) * (rank + 1);
            }

            int _rank = rank;
            prob.getFeatures().forEach(f -> {
                double v = prob.p(f) * (_rank + 1);
                int c = count.getInt(f);
                if (v >= c) {
                    disprop[0] += (v - c) * (v - c);
                }
                ideal[0] += v * v;
            });

            cpr += 1 - disprop[0] / ideal[0];

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        cpr /= rank;

        return cpr;
    }
}
