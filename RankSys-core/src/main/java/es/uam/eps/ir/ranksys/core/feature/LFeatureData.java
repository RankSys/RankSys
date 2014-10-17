/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.IndexedFeature;
import es.uam.eps.ir.ranksys.core.IdxValuePair;
import es.uam.eps.ir.ranksys.core.IndexedUser;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public interface LFeatureData<I, F, V> extends FeatureData<I, F, V>, IndexedUser<I>, IndexedFeature<F> {

    Stream<IdxValuePair<V>> getIidxFeatures(final int iidx);

    Stream<IdxValuePair<V>> getFidxItems(final int fidx);

    int numItems(int fidx);

    int numFeatures(int iidx);

}
