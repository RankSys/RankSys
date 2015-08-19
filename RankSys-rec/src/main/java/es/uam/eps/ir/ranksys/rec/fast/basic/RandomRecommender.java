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
package es.uam.eps.ir.ranksys.rec.fast.basic;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import static java.lang.Double.NaN;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.shuffle;
import java.util.List;
import java.util.Random;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Random recommender. It provides non-personalized recommendations without by extracting a sequence of a shuffled list of the items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class RandomRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final Random random;
    private final List<IdxDouble> randomList;

    /**
     * Constructor.
     *
     * @param uIndex fast user index
     * @param iIndex fast item index
     */
    public RandomRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);
        random = new Random();
        randomList = iIndex.getAllIidx()
                .mapToObj(iidx -> new IdxDouble(iidx, Double.NaN))
                .collect(toList());

        shuffle(randomList);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = randomList.size();
        }

        List<IdxDouble> recommended = new ArrayList<>();
        int s = random.nextInt(randomList.size());
        int j = s;
        for (int i = 0; i < maxLength; i++) {
            IdxDouble iv = randomList.get(j);
            while (!filter.test(iv.idx)) {
                j = (j + 1) % randomList.size();
                iv = randomList.get(j);
            }
            recommended.add(iv);
            j = (j + 1) % randomList.size();
            if (s == j) {
                break;
            }
        }

        return new FastRecommendation(uidx, recommended);
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates) {
        List<IdDouble<I>> items = candidates.map(i -> new IdDouble<>(i, NaN)).collect(toList());
        Collections.shuffle(items, random);
        
        return new Recommendation<>(u, items);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        List<IdxDouble> items = candidates.mapToObj(iidx -> new IdxDouble(iidx, NaN)).collect(toList());
        Collections.shuffle(items, random);

        return new FastRecommendation(uidx, items);
    }

}
