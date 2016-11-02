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
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.mf.plsa.PLSAFactorizer;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * PLSA aspect and intent models factory. It learns PLSA model which then is used to create aspect and intent model.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 */
public class PLSAIAFactorizationModelFactory<U, I> extends IAFactorizationModelFactory<U, I, Integer> {

    private final PLSAIntentModel intentModel;
    private final PLSAAspectModel aspectModel;

    /**
     * Creates the PLSA models factory. When called, factorizes data using PLSA.
     *
     * @param numIter number of expectation-maximization steps
     * @param data    training data
     */
    public PLSAIAFactorizationModelFactory(int numIter, int k, FastPreferenceData<U, I> data) {
        super(new NormalizedPLSAFactorizer<U, I>(numIter).factorize(k, data));
        this.intentModel = new PLSAIntentModel();
        this.aspectModel = new PLSAAspectModel(intentModel);
    }

    @Override
    public IntentModel<U, I, Integer> getIntentModel() {
        return intentModel;
    }

    @Override
    public AspectModel<U, I, Integer> getAspectModel() {
        return aspectModel;
    }

    private static class NormalizedPLSAFactorizer<U, I> extends PLSAFactorizer<U, I> {

        public NormalizedPLSAFactorizer(int numIter) {
            super(numIter);
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

    private class PLSAIntentModel extends IntentModel<U, I, Integer> {

        @Override
        protected UserIntentModel<U, I, Integer> get(U user) {
            DoubleMatrix1D userVector = getFactorization().getUserVector(user);
            return new PLSAUserIntentModel(userVector);
        }

        private class PLSAUserIntentModel implements UserIntentModel<U, I, Integer> {

            private final DoubleMatrix1D userVector;
            private final Set<Integer> nonZeroFactors;

            public PLSAUserIntentModel(DoubleMatrix1D userVector) {
                this.nonZeroFactors = new HashSet<>();
                for (int i = 0; i < userVector.size(); i++) {
                    if (userVector.getQuick(i) > 0) {
                        nonZeroFactors.add(i);
                    }
                }
                this.userVector = userVector;
            }

            @Override
            public Set<Integer> getIntents() {
                return nonZeroFactors;
            }

            @Override
            public Stream<Integer> getItemIntents(I i) {
                DoubleMatrix1D itemVector = getFactorization().getItemVector(i);
                return getIntents().stream().filter(f -> itemVector.getQuick(f) > 0.0);
            }

            @Override
            public double pf_u(Integer f) {
                return userVector.getQuick(f);
            }
        }
    }

    private class PLSAAspectModel extends AspectModel<U, I, Integer> {

        public PLSAAspectModel(PLSAIntentModel intentModel) {
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
            public ItemAspectModel<I, Integer> getItemAspectModel(List<Tuple2od<I>> items) {
                return (iv, f) -> getFactorization().getItemVector(iv.v1).getQuick(f);
            }
        }
    }
}
