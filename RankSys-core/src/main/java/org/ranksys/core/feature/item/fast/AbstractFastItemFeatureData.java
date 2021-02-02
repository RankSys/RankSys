/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.feature.item.fast;

import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.core.index.fast.FastFeatureIndex;
import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.util.tuples.Tuple2io;

/**
 * Abstract FastItemFeatureData, implementing the interfaces of FastItemIndex and
 * FastFeatureIndex by delegating to implementations of these.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information about item-feature pairs
 */
public abstract class AbstractFastItemFeatureData<I, F, V> implements FastItemFeatureData<I, F, V>
{

    private final FastItemIndex<I> ii;
    private final FastFeatureIndex<F> fi;

    /**
     * Constructor.
     *
     * @param ii item index
     * @param fi feature index
     */
    protected AbstractFastItemFeatureData(FastItemIndex<I> ii, FastFeatureIndex<F> fi) {
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
    public Stream<Tuple2<I, V>> getFeatureItems(F f) {
        return getFidxItems(feature2fidx(f)).map(this::iidx2item);
    }

    @Override
    public Stream<Tuple2<F, V>> getItemFeatures(I i) {
        return getIidxFeatures(item2iidx(i)).map(this::fidx2feature);
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

    @Override
    public <V> Tuple2<F, V> fidx2feature(Tuple2io<V> tuple) {
        return fi.fidx2feature(tuple);
    }

    @Override
    public <V> Tuple2<I, V> iidx2item(Tuple2io<V> tuple) {
        return ii.iidx2item(tuple);
    }

}
