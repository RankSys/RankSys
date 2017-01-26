/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.learner;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import org.ranksys.fm.PreferenceFM;
import org.ranksys.javafm.FM;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.learner.FMLearner;

import java.util.Arrays;
import java.util.Random;

/**
 * Learner for PreferenceFMs.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public abstract class PreferenceFMLearner<U, I> {

    private final FastUserIndex<U> users;
    private final FastItemIndex<I> items;

    /**
     * Constructor.
     *
     * @param users user index
     * @param items item index
     */
    public PreferenceFMLearner(FastUserIndex<U> users, FastItemIndex<I> items) {
        this.users = users;
        this.items = items;
    }

    protected abstract FMLearner<FMData> getLearner();

    protected abstract FMData toFMData(FastPreferenceData<U, I> preferences);

    /**
     * Trains an already existing (and possibly pre-trained) FM.
     *
     * @param fm    preference FM
     * @param train training data
     */
    public void learn(PreferenceFM<U, I> fm, FastPreferenceData<U, I> train) {
        getLearner().learn(fm.getFM(), toFMData(train));
    }

    /**
     * Trains an already existing (and possibly pre-trained) FM.
     *
     * @param fm    preference FM
     * @param train training data
     * @param test  test data (for displaying error rate in test)
     */
    public void learn(PreferenceFM<U, I> fm, FastPreferenceData<U, I> train, FastPreferenceData<U, I> test) {
        getLearner().learn(fm.getFM(), toFMData(train), toFMData(test));
    }

    /**
     * Creates and trains a preference FM.
     *
     * @param train training data
     * @param K     number of factors in FM
     * @param sdev  standard deviation for initialisation of parameters
     * @return a trained preference FM
     */
    public PreferenceFM<U, I> learn(FastPreferenceData<U, I> train, int K, double sdev) {
        FMData fmTrain = toFMData(train);
        FM fm = new FM(fmTrain.numFeatures(), K, new Random(), sdev);

        getLearner().learn(fm, fmTrain);

        return new PreferenceFM<>(users, items, fm);
    }

    /**
     * Creates and trains a preference FM.
     *
     * @param train training data
     * @param test  test data (for displaying error rate in test)
     * @param K     number of factors in FM
     * @param sdev  standard deviation for initialisation of parameters
     * @return a trained preference FM
     */
    public PreferenceFM<U, I> learn(FastPreferenceData<U, I> train, FastPreferenceData<U, I> test, int K, double sdev) {
        FMData fmTrain = toFMData(train);
        FMData fmTest = toFMData(test);
        FM fm = new FM(fmTrain.numFeatures(), K, new Random(), sdev);

        getLearner().learn(fm, fmTrain, fmTest);

        return new PreferenceFM<>(users, items, fm);
    }

    protected double[] vectorise(double v) {
        double[] a = new double[users.numUsers() + items.numItems()];
        Arrays.fill(a, v);

        return a;
    }
}
