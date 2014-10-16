/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.other.metric;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author saul
 */
public class SRecall<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final FeatureData<I, F, ?> featureData;
    private final int cutoff;
    protected final RelevanceModel<U, I> relModel;

    public SRecall(FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        this.featureData = featureData;
        this.cutoff = cutoff;
        this.relModel = relModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getUserModel(recommendation.getUser());

        Set<F> subtopics = new HashSet<>();
        
        int rank = 0;
        for (IdDoublePair<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.id)) {
                subtopics.addAll(featureData.getItemFeatures(iv.id).map(fv -> fv.id).collect(Collectors.toList()));
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        return subtopics.size() / (double) featureData.numFeatures();
    }

}
