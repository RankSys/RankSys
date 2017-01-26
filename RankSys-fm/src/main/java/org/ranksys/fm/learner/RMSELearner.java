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
import org.ranksys.fm.data.OneClassPreferenceFMData;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.learner.FMLearner;
import org.ranksys.javafm.learner.gd.PointWiseGradientDescent;

import static org.ranksys.javafm.learner.gd.PointWiseError.rmse;

/**
 * Learner for PreferenceFMs using a squared error-based stochastic gradient descent learner with one-class collaborative filtering data.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class RMSELearner<U, I> extends PreferenceFMLearner<U, I> {

    private final double learnRate;
    private final int numIter;
    private final double regB;
    private final double[] regW;
    private final double[] regM;
    private final double negativeProp;

    /**
     * Constructor.
     *
     * @param learnRate    learning rate (shrinkage)
     * @param numIter      number of iterations
     * @param regB         regularisation parameter for the global bias
     * @param regW         regularisation parameter for the feature biase
     * @param regM         regularisation parameter for factorisation
     * @param negativeProp proportion of the negative class (unbserved user-item pairs)
     * @param users        user index
     * @param items        item index
     */
    public RMSELearner(double learnRate, int numIter, double regB, double regW, double regM, double negativeProp, FastUserIndex<U> users, FastItemIndex<I> items) {
        super(users, items);
        this.learnRate = learnRate;
        this.numIter = numIter;
        this.regB = regB;
        this.regW = vectorise(regW);
        this.regM = vectorise(regM);
        this.negativeProp = negativeProp;
    }

    @Override
    protected FMLearner<FMData> getLearner() {
        return new PointWiseGradientDescent(learnRate, numIter, rmse(), regB, regW, regM);
    }

    @Override
    protected FMData toFMData(FastPreferenceData<U, I> preferences) {
        return new OneClassPreferenceFMData(preferences, negativeProp);
    }

}
