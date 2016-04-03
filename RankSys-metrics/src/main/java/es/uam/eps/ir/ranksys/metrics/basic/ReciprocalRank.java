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
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.List;
import static java.util.stream.IntStream.range;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Reciprocal Rank (RR, also known as MRR when averaged over queries/users).
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class ReciprocalRank<U, I> extends AbstractRecommendationMetric<U, I> {

    private final RelevanceModel<U, I> relModel;
    private final int cutoff;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of recommended lists
     * @param relModel relevance model
     */
    public ReciprocalRank(int cutoff, RelevanceModel<U, I> relModel) {
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
        RelevanceModel.UserRelevanceModel<U, I> urm = relModel.getModel(recommendation.getUser());

        List<Tuple2od<I>> items = recommendation.getItems();
        int r = range(0, items.size())
                .limit(cutoff)
                .filter(k -> urm.isRelevant(items.get(k).v1))
                .findFirst().orElse(-1);
        
        if (r == -1) {
            return 0;
        } else {
            return 1 / (1.0 + r);
        }
    }
}
