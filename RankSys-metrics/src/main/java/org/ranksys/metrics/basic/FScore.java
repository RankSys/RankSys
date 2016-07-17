package org.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.metrics.basic.Precision;
import es.uam.eps.ir.ranksys.metrics.basic.Recall;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;

public class FScore<U, I> extends HarmonicMean<U, I> {

    public FScore(int cutoff, IdealRelevanceModel<U, I> relModel) {
        super(new Precision<>(cutoff, relModel), new Recall<>(cutoff, relModel));
    }
}
