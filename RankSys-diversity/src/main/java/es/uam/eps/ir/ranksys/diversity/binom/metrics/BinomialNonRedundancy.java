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
public class BinomialNonRedundancy<U, I, F> extends BinomialMetric<U, I, F> {

    public BinomialNonRedundancy(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, double alpha, int cutoff, RelevanceModel<U, I> relModel) {
        super(binomialModel, featureData, alpha, cutoff, relModel);
    }

    
    @Override
    protected double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, TObjectIntMap<F> count, int nrel, int nret) {
        return nonRedundancy(prob, count, nrel);
    }

    protected static <U, I, F> double nonRedundancy(BinomialModel<U, I, F>.UserBinomialModel ubm, TObjectIntMap<F> count, int nrel) {
        if (nrel == 0 || count.isEmpty()) {
            return 0.0;
        }
        
        double nonRedundancy = ubm.getFeatures().stream().
                filter(f -> count.containsKey(f)).
                mapToDouble(f -> ubm.patience(count.get(f), f, nrel)).
                reduce(1.0, (p, q) -> p * q);
        nonRedundancy = Math.pow(nonRedundancy, 1.0 / (double) count.size());

        return nonRedundancy;
    }
}
