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
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.util.stream.Stream;

/**
 * Simple implementation of FastItemIndex backed by a bi-map IdxIndex
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public class SimpleFastItemIndex<I> implements FastItemIndex<I> {

    private final IdxIndex<I> iMap;

    /**
     * Constructor.
     *
     */
    public SimpleFastItemIndex() {
        this.iMap = new IdxIndex<>();
    }

    @Override
    public boolean containsItem(I i) {
        return iMap.containsId(i);
    }

    @Override
    public int numItems() {
        return iMap.size();
    }

    @Override
    public Stream<I> getAllItems() {
        return iMap.getIds();
    }

    @Override
    public int item2iidx(I i) {
        return iMap.get(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iMap.get(iidx);
    }

    /**
     * Add a new item to the index. If the item already exists, nothing is
     * done.
     *
     * @param i id of the item
     * @return index of the item
     */
    public int add(I i) {
        return iMap.add(i);
    }

}
