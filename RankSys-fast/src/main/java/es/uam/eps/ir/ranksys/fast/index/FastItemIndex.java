/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.index;

import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public interface FastItemIndex<I> extends ItemIndex<I> {

    @Override
    public default boolean containsItem(I i) {
        return item2iidx(i) >= 0;
    }

    @Override
    public default Stream<I> getAllItems() {
        return IntStream.range(0, numItems()).mapToObj(iidx -> iidx2item(iidx));
    }
    
    public int item2iidx(I i);

    public I iidx2item(int iidx);
}
