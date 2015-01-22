/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.core.index.FeatureIndex;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public interface FastFeatureIndex<F> extends FeatureIndex<F> {

    @Override
    public default boolean containsFeature(F f) {
        return feature2fidx(f) >= 0;
    }

    @Override
    public default Stream<F> getAllFeatures() {
        return IntStream.range(0, numFeatures()).mapToObj(fidx -> fidx2feature(fidx));
    }

    public int feature2fidx(F f);

    public F fidx2feature(int fidx);

}
