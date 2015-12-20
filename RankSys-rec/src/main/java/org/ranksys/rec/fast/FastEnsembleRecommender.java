/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.rec.fast;

import es.uam.eps.ir.ranksys.rec.fast.FastRankingRecommender;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.Map.Entry;

/**
 * Ensemble of recommenders, performs a linear combination of the scores
 * given by several recommenders.
 * 
 * TO DO: add normalization of scores prior to aggregation.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 * @param <U> user type
 * @param <I> item type
 */
public class FastEnsembleRecommender<U, I> extends FastRankingRecommender<U, I> {

    private final Iterable<Entry<FastRankingRecommender<U, I>, Double>> recommenders;

    /**
     * Constructor.
     *
     * @param recommenders a sequence of recommender-weight pairs
     */
    public FastEnsembleRecommender(Iterable<Entry<FastRankingRecommender<U, I>, Double>> recommenders) {
        super(getFirst(recommenders), getFirst(recommenders));
        this.recommenders = recommenders;
    }

    private static <U, I> FastRankingRecommender<U, I> getFirst(Iterable<Entry<FastRankingRecommender<U, I>, Double>> recommenders) {
        return recommenders.iterator().next().getKey();
    }
    
    /**
     * Returns a map of item-score pairs.
     *
     * @param uidx index of the user whose scores are predicted
     * @return a map of item-score pairs
     */
    @Override
    public Int2DoubleMap getScoresMap(int uidx) {
        Int2DoubleOpenHashMap scoresMap = new Int2DoubleOpenHashMap();
        for (Entry<FastRankingRecommender<U, I>, Double> rw : recommenders) {
            double w = rw.getValue();
            rw.getKey().getScoresMap(uidx).int2DoubleEntrySet().forEach(e -> {
                scoresMap.addTo(e.getIntKey(), w * e.getDoubleValue());
            });
        }
        
        return scoresMap;
    }

}
