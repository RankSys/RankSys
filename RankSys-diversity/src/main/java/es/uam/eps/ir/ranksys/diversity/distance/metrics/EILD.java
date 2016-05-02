/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.distance.metrics;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rank.RankingDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.List;
import java.util.function.ToDoubleFunction;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Expected Intra-List Diversity metric.
 * 
 * S. Vargas and P. Castells. Rank and relevance in novelty and diversity for
 * Recommender Systems. RecSys 2011.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class EILD<U, I> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ItemDistanceModel<I> distModel;
    private final RelevanceModel<U, I> relModel;
    private final RankingDiscountModel disc1;
    private final RankingDiscountModel disc2;

    /**
     * Constructor with a single ranking discount model.
     *
     * @param cutoff maximum length of recommendation lists to evaluate
     * @param distModel item distance model
     * @param relModel relevance model
     * @param disc ranking discount model
     */
    public EILD(int cutoff, ItemDistanceModel<I> distModel, RelevanceModel<U, I> relModel, RankingDiscountModel disc) {
        this(cutoff, distModel, relModel, disc, disc);
    }

    /**
     * Constructor with a two ranking discount models: for global ranking and
     * ranking gap between items.
     *
     * @param cutoff maximum length of recommendation lists to evaluate
     * @param distModel item distance model
     * @param relModel relevance model
     * @param disc1 ranking discount model for item ranking
     * @param disc2 ranking discount model for ranking gap
     */
    public EILD(int cutoff, ItemDistanceModel<I> distModel, RelevanceModel<U, I> relModel, RankingDiscountModel disc1, RankingDiscountModel disc2) {
        this.cutoff = cutoff;
        this.distModel = distModel;
        this.relModel = relModel;
        this.disc1 = disc1;
        this.disc2 = disc2;
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

        List<Tuple2od<I>> items = recommendation.getItems();
        int N = Math.min(cutoff, items.size());

        double eild = 0.0;
        double norm = 0;
        for (int i = 0; i < N; i++) {
            double ieild = 0.0;
            double inorm = 0.0;
            ToDoubleFunction<I> iDist = distModel.dist(items.get(i).v1);
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    continue;
                }
                double dist = iDist.applyAsDouble(items.get(j).v1);
                if (!Double.isNaN(dist)) {
                    double w = disc2.disc(Math.max(0, j - i - 1)) * userRelModel.gain(items.get(j).v1);
                    ieild += w * dist;
                    inorm += w;
                }
            }
            if (inorm > 0) {
                eild += disc1.disc(i) * userRelModel.gain(items.get(i).v1) * ieild / inorm;
            }
            norm += disc1.disc(i);
        }
        if (norm > 0) {
            eild /= norm;
        }

        return eild;
    }

}
