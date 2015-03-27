/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.rec.fast;

import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class FastRankingRecommender<U, I> extends AbstractFastRecommender<U, I> {

    public FastRankingRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (uidx == -1) {
            return new FastRecommendation(uidx, new ArrayList<>(0));
        }

        Int2DoubleMap scoresMap = getScoresMap(uidx);

        if (maxLength == 0) {
            maxLength = scoresMap.size();
        }

        final IntDoubleTopN topN = new IntDoubleTopN(maxLength);
        scoresMap.int2DoubleEntrySet().forEach(e -> {
            int iidx = e.getIntKey();
            double score = e.getDoubleValue();
            if (filter.test(iidx)) {
                topN.add(iidx, score);
            }
        });

        topN.sort();

        List<IdxDouble> items = topN.reverseStream()
                .map(e -> new IdxDouble(e))
                .collect(Collectors.toList());

        return new FastRecommendation(uidx, items);
    }

    protected abstract Int2DoubleMap getScoresMap(int uidx);
}
