/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.mf.plsa;

import cern.colt.function.DoubleFunction;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.plsa.PLSAFactorizer;

import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.sqrt;

/**
 * CPLSA factorizer. It is similar to the PLSA factorizer except it uses explicitly known aspects.
 *
 * J. Wasilewski, N. Hurley. Intent-Aware Diversification Using a Constrained PLSA. RecSys 2016.
 *
 * @author Jacek Wasilewski (jacek.wasilewski@insight-centre.org)
 *
 * @param <U> user type
 * @param <I> item type
 * @param <F> aspect type
 */
public class CPLSAFactorizer<U, I, F> extends PLSAFactorizer<U, I> {

    private final FastFeatureData<I, F, ?> featureData;

    /**
     * Constructor.
     *
     * @param numIter number of expectation-maximization steps
     * @param featureData aspects data
     */
    public CPLSAFactorizer(int numIter, FastFeatureData<I, F, ?> featureData) {
        super(numIter);
        this.featureData = featureData;
    }

    /**
     * Creates and calculates a factorization.
     *
     * @param data preference data
     * @return a matrix factorization
     */
    public Factorization<U, I> factorize(FastPreferenceData<U, I> data) {
        DoubleFunction init = x -> sqrt(1.0 / featureData.numFeatures()) * Math.random();
        Factorization<U, I> factorization = new ExplicitFactorization(data, data, featureData, init);
        factorize(factorization, data);
        return factorization;
    }

    @Override
    public Factorization<U, I> factorize(int k, FastPreferenceData<U, I> data) {
        throw new UnsupportedOperationException();
    }

    private class ExplicitFactorization extends Factorization<U, I> {

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
}
