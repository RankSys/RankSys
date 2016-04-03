/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Recall metric: proportion of relevant items in a recommendation list to all relevant items.
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @author Jacek Wasilewski (jacek.wasilewski@insightcentre.org)
 */
public class Recall<U, I> extends AbstractRecommendationMetric<U, I> {

    /**
     * Relevance model
     */
    private final IdealRelevanceModel<U, I> relModel;

    /**
     * Maximum length of recommended lists
     */
    private final int cutoff;

    /**
     * Constructor.
     *
     * @param cutoff   maximum length of recommended lists
     * @param relModel relevance model
     */
    public Recall(int cutoff, IdealRelevanceModel<U, I> relModel) {
        this.relModel = relModel;
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
        U user = recommendation.getUser();
        IdealRelevanceModel.UserIdealRelevanceModel<U, I> userRelModel = relModel.getModel(user);

        int numberOfAllRelevant = relModel.getModel(user).getRelevantItems().size();

        if (numberOfAllRelevant == 0) {
            return 0.0;
        }

        return recommendation.getItems().stream()
                .limit(cutoff)
                .map(Tuple2od::v1)
                .filter(userRelModel::isRelevant)
                .count() / (double) numberOfAllRelevant;
    }
}
