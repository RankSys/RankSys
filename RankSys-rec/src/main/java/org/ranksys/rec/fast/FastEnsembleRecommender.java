/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 */
public class FastEnsembleRecommender<U, I> extends FastRankingRecommender<U, I> {

    private final Iterable<Entry<FastRankingRecommender<U, I>, Double>> recommenders;

    public FastEnsembleRecommender(Iterable<Entry<FastRankingRecommender<U, I>, Double>> recommenders) {
        super(getFirst(recommenders), getFirst(recommenders));
        this.recommenders = recommenders;
    }

    private static <U, I> FastRankingRecommender<U, I> getFirst(Iterable<Entry<FastRankingRecommender<U, I>, Double>> recommenders) {
        return recommenders.iterator().next().getKey();
    }
    
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
