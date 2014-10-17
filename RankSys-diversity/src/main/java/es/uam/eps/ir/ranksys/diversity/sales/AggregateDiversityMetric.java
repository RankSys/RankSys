/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.sales;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author saul
 */
public class AggregateDiversityMetric<U, I> extends AbstractSystemMetric<U, I> {

    private final RelevanceModel<U, I> relModel;
    private final Set<I> recommendedItems;
    private final int cutoff;

    public AggregateDiversityMetric(int cutoff) {
        this(cutoff, new NoRelevanceModel<>());
    }

    public AggregateDiversityMetric(int cutoff, RecommenderData<U, I, Double> testData, double relThreshold) {
        this(cutoff, new BinaryRelevanceModel<>(testData, relThreshold));
    }

    public AggregateDiversityMetric(int cutoff, RelevanceModel<U, I> relModel) {
        this.relModel = relModel;
        this.recommendedItems = new HashSet<>();
        this.cutoff = cutoff;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        U u = recommendation.getUser();
        UserRelevanceModel<U, I> urm = relModel.getUserModel(u);
        
        int rank = 0;
        for (IdDoublePair<I> ivp : recommendation.getItems()) {
            if (urm.isRelevant(ivp.id)) {
                recommendedItems.add(ivp.id);
            }
            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
    }

    @Override
    public double evaluate() {
        return recommendedItems.size();
    }
}
