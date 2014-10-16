/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.binom.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 *
 * @author saul
 */
public abstract class BinomialMetric<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final BinomialModel<U, I, F> binomialModel;
    private final FeatureData<I, F, ?> featureData;
    protected final double alpha;
    private final int cutoff;
    protected final RelevanceModel<U, I> relModel;

    public BinomialMetric(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, double alpha, int cutoff, RelevanceModel<U, I> relModel) {
        this.binomialModel = binomialModel;
        this.featureData = featureData;
        this.alpha = alpha;
        this.cutoff = cutoff;
        this.relModel = relModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getUserModel(recommendation.getUser());
        BinomialModel<U, I, F>.UserBinomialModel prob = binomialModel.getUserModel(recommendation.getUser(), alpha);

        TObjectIntMap<F> count = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);

        int rank = 0;
        int nrel = 0;
        for (IdDoublePair<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.id)) {
                featureData.getItemFeatures(iv.id).forEach(fv -> {
                    count.adjustOrPutValue(fv.id, 1, 1);
                });
                nrel++;
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        return getResultFromCount(prob, count, nrel, rank);
    }

    protected abstract double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, TObjectIntMap<F> count, int nrel, int nret);

}
