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
package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.nn.sim.Similarity;
import java.util.function.IntToDoubleFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class ItemSimilarity<I> implements Similarity, FastItemIndex<I> {

    protected final FastItemIndex<I> IndexedItem;
    protected final Similarity sim;

    protected ItemSimilarity(FastItemIndex<I> indexedItem, Similarity sim) {
        this.IndexedItem = indexedItem;
        this.sim = sim;
    }

    @Override
    public int numItems() {
        return IndexedItem.numItems();
    }

    @Override
    public int item2iidx(I i) {
        return IndexedItem.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return IndexedItem.iidx2item(iidx);
    }
    
    public ToDoubleFunction<I> similarity(I i1) {
        return i2 -> sim.similarity(item2iidx(i1)).applyAsDouble(item2iidx(i2));
    }

    public double similarity(I i1, I i2) {
        return sim.similarity(item2iidx(i1), item2iidx(i2));
    }

    public Stream<IdDouble<I>> similarItems(I i) {
        return similarItems(item2iidx(i))
                .map(us -> new IdDouble<I>(iidx2item(us.idx), us.v));
    }

    public Stream<IdxDouble> similarItems(int iidx) {
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
    public Stream<IdxDouble> similarElems(int idx) {
        return sim.similarElems(idx);
    }
}
