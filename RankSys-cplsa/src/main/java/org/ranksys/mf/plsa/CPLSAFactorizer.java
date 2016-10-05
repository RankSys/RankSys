package org.ranksys.mf.plsa;

import cern.colt.function.DoubleFunction;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.plsa.PLSAFactorizer;

import static java.lang.Math.sqrt;

public class CPLSAFactorizer<U, I, F> extends PLSAFactorizer<U, I> {

    protected final FastFeatureData<I, F, ?> featureData;

    public CPLSAFactorizer(int numIter, FastFeatureData<I, F, ?> featureData) {
        super(numIter);
        this.featureData = featureData;
    }

    public Factorization<U, I> factorize(FastPreferenceData<U, I> data) {
        DoubleFunction init = x -> sqrt(1.0 / featureData.numFeatures()) * Math.random();
        Factorization<U, I> factorization = new AspectFactorization<>(data, data, featureData, init);
        factorize(factorization, data);
        return factorization;
    }

    @Override
    public Factorization<U, I> factorize(int k, FastPreferenceData<U, I> data) {
        throw new UnsupportedOperationException();

    }

//    protected void normalizePuz(DoubleMatrix2D pu_z) {
//        for (int u = 0; u < pu_z.rows(); u++) {
//            DoubleMatrix1D tmp = pu_z.viewRow(u);
//            double norm = tmp.aggregate(plus, identity);
//            if (norm != 0.0) {
//                tmp.assign(mult(1 / norm));
//            }
//        }
//    }
//
//    protected void normalizePiz(DoubleMatrix2D piz) {
//        for (int i = 0; i < piz.columns(); i++) {
//            DoubleMatrix1D tmp = piz.viewColumn(i);
//            double norm = tmp.aggregate(plus, identity);
//            if (norm != 0.0) {
//                tmp.assign(mult(1 / norm));
//            }
//        }
//    }
}
