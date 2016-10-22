package org.ranksys.diversity.intentaware;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import es.uam.eps.ir.ranksys.diversity.intentaware.AspectModel;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import org.ranksys.core.util.tuples.Tuple2od;
import org.ranksys.mf.plsa.CPLSAFactorizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cern.jet.math.Functions.identity;
import static cern.jet.math.Functions.mult;
import static cern.jet.math.Functions.plus;

public class CPLSAIAFactorizationModelFactory<U, I, F> extends IAFactorizationModelFactory<U, I, F> {

    private final CPLSAIntentModel intentModel;
    private final CPLSAAspectModel aspectModel;
    private final FastFeatureData<I, F, ?> featureData;

    public CPLSAIAFactorizationModelFactory(int numIter, FastPreferenceData<U, I> data, FastFeatureData<I, F, ?> featureData) {
        super(new NormalizedCPLSAFactorizer<U, I, F>(numIter, featureData).factorize(data));
        this.featureData = featureData;
        this.intentModel = new CPLSAIntentModel();
        this.aspectModel = new CPLSAAspectModel(intentModel);
    }

    @Override
    public IntentModel<U, I, F> getIntentModel() {
        return intentModel;
    }

    @Override
    public AspectModel<U, I, F> getAspectModel() {
        return aspectModel;
    }

    private static class NormalizedCPLSAFactorizer<U, I, F> extends CPLSAFactorizer<U, I, F> {

        NormalizedCPLSAFactorizer(int numIter, FastFeatureData<I, F, ?> featureData) {
            super(numIter, featureData);
        }

        @Override
        protected void normalizePuz(DoubleMatrix2D pu_z) {
            for (int u = 0; u < pu_z.rows(); u++) {
                DoubleMatrix1D tmp = pu_z.viewRow(u);
                double norm = tmp.aggregate(plus, identity);
                if (norm != 0.0) {
                    tmp.assign(mult(1 / norm));
                }
            }
        }

        @Override
        protected void normalizePiz(DoubleMatrix2D piz) {
            for (int i = 0; i < piz.columns(); i++) {
                DoubleMatrix1D tmp = piz.viewColumn(i);
                double norm = tmp.aggregate(plus, identity);
                if (norm != 0.0) {
                    tmp.assign(mult(1 / norm));
                }
            }
        }
    }

    private class CPLSAIntentModel extends IntentModel<U, I, F> {

        @Override
        protected UserIntentModel<U, I, F> get(U user) {
            DoubleMatrix1D userVector = getFactorization().getUserVector(user);
            return new FactorizationUserIntentModel(userVector);
        }

        private class FactorizationUserIntentModel implements UserIntentModel<U, I, F> {

            private final DoubleMatrix1D userVector;
            private final Set<F> nonZeroFactors;

            public FactorizationUserIntentModel(DoubleMatrix1D userVector) {
                Set<Integer> nonZeroFidx = new HashSet<>();
                for (int i = 0; i < userVector.size(); i++) {
                    if (userVector.getQuick(i) > 0) {
                        nonZeroFidx.add(i);
                    }
                }
                this.userVector = userVector;
                this.nonZeroFactors = nonZeroFidx.stream()
                        .map(featureData::fidx2feature)
                        .collect(Collectors.toSet());
            }

            @Override
            public Set<F> getIntents() {
                return nonZeroFactors;
            }

            @Override
            public Stream<F> getItemIntents(I i) {
                DoubleMatrix1D itemVector = getFactorization().getItemVector(i);
                return getIntents().stream().filter(f -> itemVector.getQuick(featureData.feature2fidx(f)) > 0.0);
            }

            @Override
            public double pf_u(F f) {
                return userVector.getQuick(featureData.feature2fidx(f));
            }
        }
    }

    private class CPLSAAspectModel extends AspectModel<U, I, F> {

        public CPLSAAspectModel(CPLSAIntentModel intentModel) {
            super(intentModel);
        }

        @Override
        protected LatentUserAspectModel get(U user) {
            return new LatentUserAspectModel(user);
        }

        private class LatentUserAspectModel extends UserAspectModel {

            public LatentUserAspectModel(U user) {
                super(user);
            }

            @Override
            public ItemAspectModel<I, F> getItemAspectModel(List<Tuple2od<I>> items) {
                return (iv, f) -> getFactorization().getItemVector(iv.v1).getQuick(featureData.feature2fidx(f));
            }
        }
    }
}
