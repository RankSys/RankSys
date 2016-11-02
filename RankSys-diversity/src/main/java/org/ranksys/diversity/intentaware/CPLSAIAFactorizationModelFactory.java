/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.diversity.intentaware;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
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

/**
 * CPLSA aspect and intent models factory. It learns CPLSA model which then is used to create aspect and intent model.
 *
 * J. Wasilewski, N. Hurley. Intent-Aware Diversification Using a Constrained PLSA. RecSys 2016.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 */
public class CPLSAIAFactorizationModelFactory<U, I, F> extends IAFactorizationModelFactory<U, I, F> {

    private final CPLSAIntentModel intentModel;
    private final CPLSAAspectModel aspectModel;
    private final FastFeatureData<I, F, ?> featureData;

    /**
     * Creates the CPLSA models factory. When called, factorizes data using CPLSA.
     *
     * @param numIter number of expectation-maximization steps
     * @param data training data
     * @param featureData aspects data
     */
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

        public NormalizedCPLSAFactorizer(int numIter, FastFeatureData<I, F, ?> featureData) {
            super(numIter, featureData);
        }

        /**
         * Normalizes matrix of p(z|u) such that \forall_u: \sum_z p(z|u) = 1.
         *
         * @param pu_z normalized matrix of p(z|u)
         */
        @Override
        protected void normalizePuz(DoubleMatrix2D pu_z) {
            for (int u = 0; u < pu_z.rows(); u++) {
                DoubleMatrix1D tmp = pu_z.viewRow(u);
                double norm = tmp.aggregate(Functions.plus, Functions.identity);
                if (norm != 0.0) {
                    tmp.assign(Functions.mult(1 / norm));
                }
            }
        }

        /**
         * Normalizes matrix of p(i|z) such that \forall_z: \sum_i p(i|z) = 1.
         *
         * @param piz normalized matrix of p(i|z)
         */
        @Override
        protected void normalizePiz(DoubleMatrix2D piz) {
            for (int i = 0; i < piz.columns(); i++) {
                DoubleMatrix1D tmp = piz.viewColumn(i);
                double norm = tmp.aggregate(Functions.plus, Functions.identity);
                if (norm != 0.0) {
                    tmp.assign(Functions.mult(1 / norm));
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
