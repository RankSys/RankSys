/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.uam.eps.ir.ranksys.diversity.binom.metrics;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import gnu.trove.map.TObjectIntMap;

/**
 *
 * @author saul
 */
public class BinomialDiversity<U, I, F> extends BinomialMetric<U, I, F> {

    public BinomialDiversity(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, double alpha, int cutoff, RelevanceModel<U, I> relModel) {
        super(binomialModel, featureData, alpha, cutoff, relModel);
    }

    @Override
    protected double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, TObjectIntMap<F> count, int nrel, int nret) {
        return BinomialNonRedundancy.nonRedundancy(prob, count, nrel) * BinomialCoverage.coverage(prob, count, nret);
    }
}
