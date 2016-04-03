/* 
 * Copyright (C) 2015 RankSys (http://ranksys.org)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.reranking;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.novdiv.reranking.PermutationReranker;
import static java.lang.Math.log;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import java.util.List;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Dithering re-ranker. It re-ranks the output of a recommendation by adding
 * gaussian noise.
 * <br>
 * Check: https://buildingrecommenders.wordpress.com/2015/11/11/dithering/
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class DitheringReranker<U, I> extends PermutationReranker<U, I> {

    private final double variance;

    /**
     * Constructor.
     * 
     * @param variance variance of the gaussian noise to be added to the original
     * recommendation scores.
     */
    public DitheringReranker(double variance) {
        this.variance = variance;
    }

    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation, int maxLength) {
        List<Tuple2od<I>> items = recommendation.getItems();
        int M = items.size();
        int N = min(maxLength, M);

        if (variance == 0.0) {
            return getBasePerm(N);
        }
        
        NormalDistribution dist = new NormalDistribution(0.0, sqrt(variance));

        IntDoubleTopN topN = new IntDoubleTopN(N);
        for (int i = 0; i < M; i++) {
            topN.add(M - i, log(i + 1) + dist.sample());
        }
        topN.sort();

        int[] perm = topN.stream()
                .mapToInt(e -> M - e.v1)
                .toArray();

        return perm;
    }
}
