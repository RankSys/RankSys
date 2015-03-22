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

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import java.util.ArrayList;
import static java.util.Collections.shuffle;
import java.util.List;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class RandomRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final Random random;
    private final List<IdxDouble> randomList;

    public RandomRecommender(FastPreferenceData<U, I, ?> data) {
        super(data);
        random = new Random();
        randomList = data.getAllIidx().mapToObj(iidx -> new IdxDouble(iidx, Double.NaN)).collect(Collectors.toList());

        shuffle(randomList);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = randomList.size();
        }
        
        List<IdxDouble> recommended = new ArrayList<>();
        int j = random.nextInt(randomList.size());
        for (int i = 0; i < maxLength; i++) {
            IdxDouble iv = randomList.get(j);
            while (!filter.test(iv.idx)) {
                j = (j + 1) % randomList.size();
                iv = randomList.get(j);
            }
            recommended.add(iv);
            j = (j + 1) % randomList.size();
        }

        return new FastRecommendation(uidx, recommended);
    }
}
