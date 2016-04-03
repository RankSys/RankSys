/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.nn.sim.Similarity;
import java.util.function.IntToDoubleFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Item similarity. It wraps a generic fast similarity and a fast item index.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public abstract class ItemSimilarity<I> implements Similarity, FastItemIndex<I> {

    /**
     * Fast item index.
     */
    protected final FastItemIndex<I> iIndex;

    /**
     * Generic fast similarity.
     */
    protected final Similarity sim;

    /**
     * Constructor.
     *
     * @param iIndex fast item index
     * @param sim generic fast similarity
     */
    protected ItemSimilarity(FastItemIndex<I> iIndex, Similarity sim) {
        this.iIndex = iIndex;
        this.sim = sim;
    }

    @Override
    public int numItems() {
        return iIndex.numItems();
    }

    @Override
    public int item2iidx(I i) {
        return iIndex.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iIndex.iidx2item(iidx);
    }
    
    /**
     * Returns a function returning similarities with the item
     *
     * @param i1 item
     * @return a function returning similarities with the item
     */
    public ToDoubleFunction<I> similarity(I i1) {
        return i2 -> sim.similarity(item2iidx(i1)).applyAsDouble(item2iidx(i2));
    }

    /**
     * Returns the similarity between a pair of items.
     *
     * @param i1 first item
     * @param i2 second item
     * @return similarity value between the items
     */
    public double similarity(I i1, I i2) {
        return sim.similarity(item2iidx(i1), item2iidx(i2));
    }

    /**
     * Returns all the items that are similar to the item.
     *
     * @param i item
     * @return a stream of item-similarity pairs
     */
    public Stream<Tuple2od<I>> similarItems(I i) {
        return similarItems(item2iidx(i))
                .map(this::iidx2item);
    }

    /**
     * Returns all the items that are similar to the item - fast version.
     *
     * @param iidx item
     * @return a stream of item-similarity pairs
     */
    public Stream<Tuple2id> similarItems(int iidx) {
        return sim.similarElems(iidx);
    }

    @Override
    public IntToDoubleFunction similarity(int idx1) {
        return sim.similarity(idx1);
    }

    @Override
    public double similarity(int idx1, int idx2) {
        return sim.similarity(idx1, idx2);
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx) {
        return sim.similarElems(idx);
    }
}
