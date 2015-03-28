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
package es.uam.eps.ir.ranksys.novdiv.distance;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

/**
 * Feature-based item distance model.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information between item-feature pairs
 */
public abstract class FeatureItemDistanceModel<I, F, V> implements ItemDistanceModel<I> {

    private final FeatureData<I, F, V> featureData;

    /**
     * Constructor.
     *
     * @param featureData feature data
     */
    public FeatureItemDistanceModel(FeatureData<I, F, V> featureData) {
        this.featureData = featureData;
    }

    @Override
    public ToDoubleFunction<I> dist(I i) {
        Stream<IdObject<F, V>> features1 = featureData.getItemFeatures(i);
        ToDoubleFunction<Stream<IdObject<F, V>>> iDist = dist(features1);
        return j -> {
            return iDist.applyAsDouble(featureData.getItemFeatures(j));
        };
    }

    /**
     * Returns a function that returns the feature-based similarity to the
     * features of an item.
     *
     * @param features1 stream of features of an item
     * @return function that returns the feature-based similarity to the
     * features of an item
     */
    protected abstract ToDoubleFunction<Stream<IdObject<F, V>>> dist(Stream<IdObject<F, V>> features1);
}
