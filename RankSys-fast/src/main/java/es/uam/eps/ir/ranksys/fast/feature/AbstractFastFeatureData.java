/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.feature;

import es.uam.eps.ir.ranksys.core.IdVar;
import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public abstract class AbstractFastFeatureData<I, F, V> implements FastFeatureData<I, F, V> {

    private final FastItemIndex<I> ii;
    private final FastFeatureIndex<F> fi;

    protected AbstractFastFeatureData(FastItemIndex<I> ii, FastFeatureIndex<F> fi) {
        this.ii = ii;
        this.fi = fi;
    }

    @Override
    public Stream<I> getItemsWithFeatures() {
        return getIidxWithFeatures().mapToObj(this::iidx2item);
    }

    @Override
    public Stream<F> getFeaturesWithItems() {
        return getFidxWithItems().mapToObj(this::fidx2feature);
    }

    @Override
    public int numFeatures(I i) {
        return numFeatures(item2iidx(i));
    }

    @Override
    public int numItems(F f) {
        return numItems(feature2fidx(f));
    }

    @Override
    public Stream<IdVar<I, V>> getFeatureItems(F f) {
        return getFidxItems(feature2fidx(f)).map(iv -> new IdVar<>(iidx2item(iv.idx), iv.v));
    }

    @Override
    public Stream<IdVar<F, V>> getItemFeatures(I i) {
        return getIidxFeatures(item2iidx(i)).map(fv -> new IdVar<>(fidx2feature(fv.idx), fv.v));
    }

    @Override
    public boolean containsItem(I i) {
        return ii.containsItem(i);
    }

    @Override
    public int numItems() {
        return ii.numItems();
    }

    @Override
    public Stream<I> getAllItems() {
        return ii.getAllItems();
    }

    @Override
    public boolean containsFeature(F f) {
        return fi.containsFeature(f);
    }

    @Override
    public int numFeatures() {
        return fi.numFeatures();
    }

    @Override
    public Stream<F> getAllFeatures() {
        return fi.getAllFeatures();
    }

    @Override
    public int item2iidx(I i) {
        return ii.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return ii.iidx2item(iidx);
    }

    @Override
    public int feature2fidx(F f) {
        return fi.feature2fidx(f);
    }

    @Override
    public F fidx2feature(int fidx) {
        return fi.fidx2feature(fidx);
    }

}
