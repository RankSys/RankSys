/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author saul
 */
public class SimpleFastFeatureIndex<F> implements FastFeatureIndex<F> {

    private final IdxIndex<F> fMap;

    public SimpleFastFeatureIndex() {
        this.fMap = new IdxIndex<>();
    }

    @Override
    public boolean containsFeature(F f) {
        return fMap.containsId(f);
    }

    @Override
    public int numFeatures() {
        return fMap.size();
    }

    @Override
    public Stream<F> getAllFeatures() {
        return StreamSupport.stream(fMap.getIds().spliterator(), false);
    }

    @Override
    public int feature2fidx(F f) {
        return fMap.get(f);
    }

    @Override
    public F fidx2feature(int fidx) {
        return fMap.get(fidx);
    }

    public int add(F f) {
        return fMap.add(f);
    }

}
