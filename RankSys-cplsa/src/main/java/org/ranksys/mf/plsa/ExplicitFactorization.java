package org.ranksys.mf.plsa;

import cern.colt.function.DoubleFunction;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.mf.Factorization;

import java.util.Set;
import java.util.stream.Collectors;

public class ExplicitFactorization<U, I, F> extends Factorization<U, I> {

    public ExplicitFactorization(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, 
            FastFeatureData<I, F, ?> featureData, DoubleFunction initFunction) {
        super(uIndex, iIndex, featureData.numFeatures(), initFunction);
        iIndex.getAllIidx().forEach(iidx -> {
            Set<Integer> itemFidxs = featureData.getIidxFeatures(iidx)
                    .map(f -> f.v1)
                    .collect(Collectors.toSet());
            featureData.getAllFidx()
                    .filter(f -> !itemFidxs.contains(f))
                    .forEach(fidx -> {
                this.itemMatrix.setQuick(iidx, fidx, 0.0);
            });
        });
    }
}