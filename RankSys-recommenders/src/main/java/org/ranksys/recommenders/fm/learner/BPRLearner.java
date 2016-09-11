package org.ranksys.recommenders.fm.learner;

import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.learner.FMLearner;
import org.ranksys.javafm.learner.gd.BPR;
import org.ranksys.recommenders.fm.data.BPRPreferenceFMData;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class BPRLearner<U, I> extends PreferenceFMLearner<U, I> {

    private final double learnRate;
    private final int numIter;
    private final double[] regW;
    private final double[] regM;

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
