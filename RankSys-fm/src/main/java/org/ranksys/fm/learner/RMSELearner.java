package org.ranksys.fm.learner;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import org.ranksys.fm.data.OneClassPreferenceFMData;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.learner.FMLearner;
import static org.ranksys.javafm.learner.gd.PointWiseError.rmse;
import org.ranksys.javafm.learner.gd.PointWiseGradientDescent;

/**
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
