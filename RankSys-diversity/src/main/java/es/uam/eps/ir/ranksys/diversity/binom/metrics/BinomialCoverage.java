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
public class BinomialCoverage<U, I, F> extends BinomialMetric<U, I, F> {

    public BinomialCoverage(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, double alpha, int cutoff, RelevanceModel<U, I> relModel) {
        super(binomialModel, featureData, alpha, cutoff, relModel);
    }

    @Override
    protected double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, TObjectIntMap<F> count, int nrel, int nret) {
        return coverage(prob, count, nret);
    }

    protected static <U, I, F> double coverage(BinomialModel<U, I, F>.UserBinomialModel ubm, TObjectIntMap<F> count, int nret) {
        double coverage = ubm.getFeatures().stream().
                filter(f -> !count.containsKey(f)).
                mapToDouble(f -> ubm.longing(f, nret)).
                reduce(1.0, (p, q) -> p * q);
        coverage = Math.pow(coverage, 1.0 / (double) ubm.getFeatures().size());
        
        return coverage;
    }
}
