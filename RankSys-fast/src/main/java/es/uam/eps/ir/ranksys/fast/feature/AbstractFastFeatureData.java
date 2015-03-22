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
package es.uam.eps.ir.ranksys.fast.feature;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import java.util.stream.Stream;

/**
 * Abstract FastFeatureData, implementing the interfaces of FastItemIndex and
 * FastFeatureIndex by delegating to implementations of these.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information about item-feature pairs
 */
public abstract class AbstractFastFeatureData<I, F, V> implements FastFeatureData<I, F, V> {

    private final FastItemIndex<I> ii;
    private final FastFeatureIndex<F> fi;

    /**
     * Constructor.
     *
     * @param ii item index
     * @param fi feature index
     */
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
    public Stream<IdObject<I, V>> getFeatureItems(F f) {
        return getFidxItems(feature2fidx(f)).map(iv -> new IdObject<>(iidx2item(iv.idx), iv.v));
    }

    @Override
    public Stream<IdObject<F, V>> getItemFeatures(I i) {
        return getIidxFeatures(item2iidx(i)).map(fv -> new IdObject<>(fidx2feature(fv.idx), fv.v));
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
