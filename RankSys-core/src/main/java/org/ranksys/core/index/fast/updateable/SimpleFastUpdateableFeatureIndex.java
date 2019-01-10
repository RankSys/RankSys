/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index.fast.updateable;

import java.util.stream.Stream;
import org.ranksys.core.index.fast.SimpleFastFeatureIndex;


/**
 * Simple implementation of FastUpdateableFeatureIndex backed by a bi-map IdxIndex
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <F> type of the features
 */
public class SimpleFastUpdateableFeatureIndex<F> extends SimpleFastFeatureIndex<F> implements FastUpdateableFeatureIndex<F> 
{
    @Override
    public int addFeature(F f)
    {
        return this.add(f);
    }
    
    /*@Override
    public int removeFeature(F f)
    {
        return this.remove(f);
    }*/
    
    /**
     * Creates a feature index from a stream of feature objects.
     *
     * @param <F> type of the features
     * @param features stream of feature objects
     * @return a fast feature index
     */
    public static <F> SimpleFastUpdateableFeatureIndex<F> load(Stream<F> features) {
        SimpleFastUpdateableFeatureIndex<F> featureIndex = new SimpleFastUpdateableFeatureIndex<>();
        features.forEach(featureIndex::add);
        return featureIndex;
    }
}
