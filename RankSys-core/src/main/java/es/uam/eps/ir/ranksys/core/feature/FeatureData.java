/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.index.FeatureIndex;
import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;

/**
 * Item-feature data.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information about item-feature pairs
 */
public interface FeatureData<I, F, V> extends ItemIndex<I>, FeatureIndex<F> {

    /**
     * Returns the number of items with features.
     *
     * @return number of items with features
     */
    int numItemsWithFeatures();

    /**
     * Returns the number of features with items.
     *
     * @return number of features with items
     */
    int numFeaturesWithItems();
    
    /**
     * Returns a stream of items with features.
     *
     * @return stream of items with features
     */
    Stream<I> getItemsWithFeatures();
    
    /**
     * Returns a stream of features with items.
     *
     * @return stream of features with items.
     */
    Stream<F> getFeaturesWithItems();
    
    /**
     * Returns a stream of items with the feature.
     *
     * @param f feature
     * @return stream of items with the feature.
     */
    Stream<Tuple2<I, V>> getFeatureItems(final F f);

    /**
     * Returns a stream of features of the item.
     *
     * @param i item
     * @return stream of features of the item.
     */
    Stream<Tuple2<F, V>> getItemFeatures(final I i);

    /**
     * Returns the number of features of the item.
     *
     * @param i item
     * @return number of features of the item
     */
    int numFeatures(I i);

    /**
     * Returns the number of items with the feature.
     *
     * @param f feature
     * @return number of items with the feature
     */
    int numItems(F f);

}
