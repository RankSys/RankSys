/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel.UserIdealRelevanceModel;
import static java.lang.Math.min;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Average Precision: average of the precision at each recall level.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class AveragePrecision<U, I> extends AbstractRecommendationMetric<U, I> {

    private final IdealRelevanceModel<U, I> relModel;
    private final int cutoff;

    /**
     * Constructor.
     *
     * @param cutoff cutoff of the metric
     * @param relevanceModel relevance model
     */
    public AveragePrecision(int cutoff, IdealRelevanceModel<U, I> relevanceModel) {
        super();
        this.relModel = relevanceModel;
        this.cutoff = cutoff;
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        UserIdealRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());

        double ap = 0;
        int relCount = 0;
        int rank = 0;

        for (Tuple2od<I> pair : recommendation.getItems()) {
            rank++;
            if (userRelModel.isRelevant(pair.v1)) {
                relCount++;
                ap += relCount / (double) rank;
            }
            if (rank == cutoff) {
                break;
            }
        }

        return ap / (double) min(cutoff, userRelModel.getRelevantItems().size());
    }
}
