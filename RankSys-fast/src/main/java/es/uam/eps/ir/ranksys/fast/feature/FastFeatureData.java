/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.feature;

import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2io;

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
    Stream<Tuple2io<V>> getIidxFeatures(final int iidx);

    /**
     * Returns the items having a feature.
     *
     * @param fidx feature index
     * @return items having the feature
     */
    Stream<Tuple2io<V>> getFidxItems(final int fidx);

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
