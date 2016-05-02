/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.fast;

import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Recommender for top-n recommendations. It selects and orders the items whose
 * predicted scores are among the n greatest.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class FastRankingRecommender<U, I> extends AbstractFastRecommender<U, I> {

    /**
     * Constructor.
     *
     * @param uIndex user index
     * @param iIndex item index
     */
    public FastRankingRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (uidx == -1) {
            return new FastRecommendation(uidx, new ArrayList<>(0));
        }

        Int2DoubleMap scoresMap = getScoresMap(uidx);

        final IntDoubleTopN topN = new IntDoubleTopN(min(maxLength, scoresMap.size()));
        scoresMap.int2DoubleEntrySet().forEach(e -> {
            int iidx = e.getIntKey();
            double score = e.getDoubleValue();
            if (filter.test(iidx)) {
                topN.add(iidx, score);
            }
        });

        topN.sort();

        List<Tuple2id> items = topN.reverseStream()
                .collect(toList());

        return new FastRecommendation(uidx, items);
    }

    /**
     * Returns a map of item-score pairs.
     *
     * @param uidx index of the user whose scores are predicted
     * @return a map of item-score pairs
     */
    public abstract Int2DoubleMap getScoresMap(int uidx);
}
