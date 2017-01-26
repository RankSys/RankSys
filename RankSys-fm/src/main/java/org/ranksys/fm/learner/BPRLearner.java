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
import org.ranksys.fm.data.BPRPreferenceFMData;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.learner.FMLearner;
import org.ranksys.javafm.learner.gd.BPR;

/**
 * Learner for PreferenceFMs using Bayesian Probabilistic Ranking.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class BPRLearner<U, I> extends PreferenceFMLearner<U, I> {

    private final double learnRate;
    private final int numIter;
    private final double[] regW;
    private final double[] regM;

    /**
     * Constructor.
     *
     * @param learnRate    learning rate (shrinkage)
     * @param numIter      number of iterations
     * @param regW         regularisation parameter for the feature biase
     * @param regM         regularisation parameter for factorisation
     * @param users        user index
     * @param items        item index
     */
    public BPRLearner(double learnRate, int numIter, double regW, double regM, FastUserIndex<U> users, FastItemIndex<I> items) {
        super(users, items);
        this.learnRate = learnRate;
        this.numIter = numIter;
        this.regW = vectorise(regW);
        this.regM = vectorise(regM);
    }

    @Override
    protected FMLearner<FMData> getLearner() {
        return new BPR(learnRate, numIter, regW, regM);
    }

    @Override
    protected FMData toFMData(FastPreferenceData<U, I> preferences) {
        return new BPRPreferenceFMData(preferences);
    }

}
