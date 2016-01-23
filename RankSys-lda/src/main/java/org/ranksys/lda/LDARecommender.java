/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.lda;

import cc.mallet.topics.ParallelTopicModel;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

/**
 * LDA recommender.  See ParallelTopicModel in Mallet (http://mallet.cs.umass.edu/) for more details.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 * 
 * @param <U> user type
 * @param <I> item type
 */
public class LDARecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final ParallelTopicModel topicModel;

    /**
     * Constructor
     *
     * @param uIndex user index
     * @param iIndex item index
     * @param topicModel LDA topic model
     */
    public LDARecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, ParallelTopicModel topicModel) {
        super(uIndex, iIndex);
        this.topicModel = topicModel;
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = numItems();
        }
        IntDoubleTopN topN = new IntDoubleTopN(maxLength);

        for (int iidx = 0; iidx < numItems(); iidx++) {
            if (filter.test(iidx)) {
                topN.add(iidx, score(topicModel, uidx, iidx));
            }
        }

        topN.sort();

        List<IdxDouble> items = topN.reverseStream()
                .map(e -> new IdxDouble(e))
                .collect(Collectors.toList());

        return new FastRecommendation(uidx, items);
    }

    private double score(ParallelTopicModel topicModel, int uidx, int iidx) {
        double[] pu = topicModel.getTopicProbabilities(uidx);
        int[] qi = topicModel.typeTopicCounts[iidx];

        double score = 0.0;
        int i = 0;
        while (i < qi.length && qi[i] > 0) {
            int z = qi[i] & topicModel.topicMask;
            int n = qi[i] >> topicModel.topicBits;

            score += pu[z] * (n / (double) topicModel.tokensPerTopic[z]);
            i++;
        }
        
        return score;
    }
    
}
