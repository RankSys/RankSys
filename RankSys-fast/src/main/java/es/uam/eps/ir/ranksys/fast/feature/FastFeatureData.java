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
package es.uam.eps.ir.ranksys.fast.feature;

import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.IdxObject;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast version of FeatureData, where item-feature relationships are
 * stored internally by the indexes provided by FastItemIndex and FastFeatureIndex.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information about item-feature pairs
 */
public interface FastFeatureData<I, F, V> extends FeatureData<I, F, V>, FastItemIndex<I>, FastFeatureIndex<F> {

    /**
     * Returns the features associated with an item.
     *
     * @param iidx item index
     * @return features associated with the item
     */
    Stream<IdxObject<V>> getIidxFeatures(final int iidx);

    /**
     * Returns the items having a feature.
     *
     * @param fidx feature index
     * @return items having the feature
     */
    Stream<IdxObject<V>> getFidxItems(final int fidx);

    /**
     * Returns the number of items having a feature.
     *
     * @param fidx feature index
     * @return number of items having the feature
     */
    int numItems(int fidx);

    /**
     * Returns the number of features associated with an item.
     *
     * @param iidx item index
     * @return number of features associated with the item
     */
    int numFeatures(int iidx);

    /**
     * Returns the indexes of the items with features.
     *
     * @return a stream of indexes of items with features
     */
    IntStream getIidxWithFeatures();
    
    /**
     * Returns the features that are associated with items.
     *
     * @return a stream of indexes of features with items
     */
    IntStream getFidxWithItems();
}
