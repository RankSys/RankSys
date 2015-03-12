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
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Filters {

    public static <U, I> Function<U, Predicate<I>> all() {
        return user -> item -> true;
    }

    public static <U, I> Function<U, Predicate<I>> notInTrain(RecommenderData<U, I, ?> trainData) {
        return user -> {
            Set<I> set = trainData.getUserPreferences(user).map(iv -> iv.id).collect(Collectors.toSet());

            return i -> !set.contains(i);
        };
    }

    public static <U, I, F> Function<U, Predicate<I>> withFeatures(FeatureData<I, F, ?> featureData) {
        throw new UnsupportedOperationException("you need to fix this, include a getItemsWithFeatures");
//        this.itemsWithFeatures = featureData.getAllItems().filter(i -> featureData.numFeatures(i) > 0).collect(Collectors.toSet());
//        return user -> {
//            return item -> itemsWithFeatures.contains(item);
//        };
    }

    public static <U, I> Function<U, Predicate<I>> and(Function<U, Predicate<I>>... filters) {
        return user -> {
            Predicate<I> andPredicate = item -> true;
            for (Function<U, Predicate<I>> filter : filters) {
                andPredicate = andPredicate.and(filter.apply(user));
            }
            return andPredicate;
        };
    }
}
