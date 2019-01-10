/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.index.fast.updateable;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.index.fast.FastFeatureIndex;
import org.ranksys.core.index.updateable.UpdateableFeatureIndex;

/**
 * Fast and updateable version of a FeatureIndex, where features are internally represented with numerical indices from 0 (inclusive) to the number of indexed features (exclusive).
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <F> type of the features
 */
public interface FastUpdateableFeatureIndex<F> extends UpdateableFeatureIndex<F>, FastFeatureIndex<F>
{
    @Override
    public default boolean containsFeature(F f) 
    {
        return feature2fidx(f) >= 0;
    }

    @Override
    public default Stream<F> getAllFeatures() 
    {
        return IntStream.range(0, numFeatures()).mapToObj(fidx -> fidx2feature(fidx));
    }
}
