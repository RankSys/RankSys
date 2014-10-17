/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;

/**
 *
 * @author saul
 */
public class Precision<U, I> extends AbstractRecommendationMetric<U, I> {

    private final RelevanceModel<U, I> relModel;
    private final int cutoff;

    public Precision(int cutoff, RecommenderData<U, I, Double> testData, double relThreshold) {
        this.relModel = new BinaryRelevanceModel<>(testData, relThreshold);
        this.cutoff = cutoff;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel userRelModel = relModel.getUserModel(recommendation.getUser());
        
        int relCount = 0;
        int rank = 0;

        for (IdDoublePair<I> pair : recommendation.getItems()) {
            if (userRelModel.isRelevant(pair.id)) {
                relCount++;
            }
            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        return relCount / (double) cutoff;
    }
}
