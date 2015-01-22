/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.feature;

import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.IdxVar;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public interface FastFeatureData<I, F, V> extends FeatureData<I, F, V>, FastItemIndex<I>, FastFeatureIndex<F> {

    Stream<IdxVar<V>> getIidxFeatures(final int iidx);

    Stream<IdxVar<V>> getFidxItems(final int fidx);

    int numItems(int fidx);

    int numFeatures(int iidx);

}
