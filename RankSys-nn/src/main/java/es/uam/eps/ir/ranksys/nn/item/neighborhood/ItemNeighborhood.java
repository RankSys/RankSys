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
package es.uam.eps.ir.ranksys.nn.item.neighborhood;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.nn.neighborhood.Neighborhood;
import java.util.stream.Stream;
import static java.util.stream.StreamSupport.stream;

/**
 * Item neighborhood. Wraps a generic neighborhood and a fast item index.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public abstract class ItemNeighborhood<I> implements Neighborhood, FastItemIndex<I> {

    /**
     * Fast item index.
     */
    protected final FastItemIndex<I> iIndex;

    /**
     * Generic neighborhood.
     */
    protected final Neighborhood neighborhood;

    /**
     * Constructor.
     *
     * @param iIndex fast item index
     * @param neighborhood generic fast neighborhood
     */
    public ItemNeighborhood(FastItemIndex<I> iIndex, Neighborhood neighborhood) {
        this.iIndex = iIndex;
        this.neighborhood = neighborhood;
    }

    @Override
    public int numItems() {
        return iIndex.numItems();
    }

    @Override
    public I iidx2item(int iidx) {
        return iIndex.iidx2item(iidx);
    }

    @Override
    public int item2iidx(I i) {
        return iIndex.item2iidx(i);
    }

    @Override
    public Stream<IdxDouble> getNeighbors(int idx) {
        return neighborhood.getNeighbors(idx);
    }

    /**
     * Returns a stream of item neighbors
     *
     * @param i item whose neighborhood is returned
     * @return a stream of item-score pairs
     */
    public Stream<IdDouble<I>> getNeighbors(I i) {
        return stream(getNeighbors(item2iidx(i)).spliterator(), false)
                .map(iv -> new IdDouble<>(iidx2item(iv.idx), iv.v));
    }
}
