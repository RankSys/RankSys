package org.ranksys.mf.plsa;

import cern.colt.function.DoubleFunction;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.plsa.PLSAFactorizer;

import static java.lang.Math.sqrt;

public class CPLSAFactorizer<U, I, F> extends PLSAFactorizer<U, I> {

    private final FastFeatureData<I, F, ?> featureData;

    public CPLSAFactorizer(int numIter, FastFeatureData<I, F, ?> featureData) {
        super(numIter);
        this.featureData = featureData;
    }

    public Factorization<U, I> factorize(FastPreferenceData<U, I> data) {
        DoubleFunction init = x -> sqrt(1.0 / featureData.numFeatures()) * Math.random();
        Factorization<U, I> factorization = new ExplicitFactorization<>(data, data, featureData, init);
        factorize(factorization, data);
        return factorization;
    }

    @Override
    public Factorization<U, I> factorize(int k, FastPreferenceData<U, I> data) {
        throw new UnsupportedOperationException();
    }
}
