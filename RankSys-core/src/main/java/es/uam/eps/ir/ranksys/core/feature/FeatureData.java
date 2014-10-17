/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public interface FeatureData<I, F, V> {

    Stream<F> getAllFeatures();

    Stream<I> getAllItems();

    Stream<IdValuePair<I, V>> getFeatureItems(final F f);

    Stream<IdValuePair<F, V>> getItemFeatures(final I i);

    int numFeatures();

    int numFeatures(I i);

    int numItems();

    int numItems(F f);

}
