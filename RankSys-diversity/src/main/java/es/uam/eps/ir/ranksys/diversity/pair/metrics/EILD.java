/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.pair.metrics;

import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.pair.ItemDistanceModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author saul
 */
public class EILD<U, I> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ItemDistanceModel<I> distModel;
    private final RelevanceModel<U, I> relModel;

    public EILD(int cutoff, ItemDistanceModel<I> distModel, RelevanceModel<U, I> relModel) {
        this.cutoff = cutoff;
        this.distModel = distModel;
        this.relModel = relModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getUserModel(recommendation.getUser());

        List<I> items = recommendation.getItems().stream().map(is -> is.id).collect(Collectors.toList());
        int N = Math.min(cutoff, items.size());

        double eild = 0.0;
        double norm = 0;
        for (int i = 0; i < N; i++) {
            double ieild = 0.0;
            double inorm = 0.0;
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    continue;
                }
                double dist = distModel.dist(items.get(i), items.get(j));
                if (!Double.isNaN(dist)) {
                    ieild += userRelModel.gain(items.get(j)) * dist;
                    inorm += userRelModel.gain(items.get(j));
                }
            }
            if (inorm > 0) {
                eild += userRelModel.gain(items.get(i)) * ieild / inorm;
                norm++;
            }
        }
        if (norm > 0) {
            eild /= norm;
        }

        return eild;
    }

}
