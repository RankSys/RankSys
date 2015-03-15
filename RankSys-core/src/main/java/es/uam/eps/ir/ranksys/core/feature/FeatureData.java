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
package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.index.FeatureIndex;
import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import java.util.stream.Stream;

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
    Stream<IdObject<I, V>> getFeatureItems(final F f);

    /**
     * Returns a stream of features of the item.
     *
     * @param i item
     * @return stream of features of the item.
     */
    Stream<IdObject<F, V>> getItemFeatures(final I i);

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
