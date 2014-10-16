/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.novelty.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.novelty.ItemNovelty;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;

/**
 *
 * @author saul
 */
public abstract class ItemNoveltyMetric<U, I> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ItemNovelty<U, I> novelty;
    private final RelevanceModel<U, I> relModel;

    public ItemNoveltyMetric(int cutoff, ItemNovelty<U, I> novelty, RelevanceModel<U, I> relevanceModel) {
        super();
        this.cutoff = cutoff;
        this.novelty = novelty;
        this.relModel = relevanceModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        U u = recommendation.getUser();
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getUserModel(u);
        
        double nov = 0;

        int rank = 0;
        for (IdDoublePair<I> iv : recommendation.getItems()) {
            nov += userRelModel.gain(iv.id) * novelty.novelty(iv.id, u);
            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        nov /= rank;

        return nov;
    }

}
