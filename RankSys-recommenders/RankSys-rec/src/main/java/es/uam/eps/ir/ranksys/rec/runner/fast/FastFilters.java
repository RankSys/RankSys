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
package es.uam.eps.ir.ranksys.rec.runner.fast;

import es.uam.eps.ir.ranksys.fast.data.FastRecommenderData;
import es.uam.eps.ir.ranksys.fast.feature.FastFeatureData;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FastFilters {

    public static IntFunction<IntPredicate> all() {
        return uidx -> iidx -> true;
    }

    public static <U, I> Function<U, IntPredicate> notInTrain(FastRecommenderData<U, I, ?> trainData) {
        return user -> {
            TIntSet set = new TIntHashSet();
            trainData.getUidxPreferences(trainData.user2uidx(user)).mapToInt(iv -> iv.idx).forEach(set::add);

            return iidx -> !set.contains(iidx);
        };
    }

    public static <U, I, F> Function<U, IntPredicate> withFeatures(FastFeatureData<I, F, ?> featureData) {
        TIntSet itemsWithFeatures = new TIntHashSet();
        featureData.getIidxWithFeatures().forEach(iidx -> itemsWithFeatures.add(iidx));
        return user -> iidx -> itemsWithFeatures.contains(iidx);
    }

    @SafeVarargs
    public static <U> Function<U, IntPredicate> and(Function<U, IntPredicate>... filters) {
        return user -> {
            IntPredicate andPredicate = iidx -> true;
            for (Function<U, IntPredicate> filter : filters) {
                andPredicate = andPredicate.and(filter.apply(user));
            }
            return andPredicate;
        };
    }
}
