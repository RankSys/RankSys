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
public class SimpleFastItemIndex<I> implements FastItemIndex<I> {

    private final IdxIndex<I> iMap;

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
        return StreamSupport.stream(iMap.getIds().spliterator(), false);
    }

    @Override
    public int item2iidx(I i) {
        return iMap.get(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iMap.get(iidx);
    }

    public int add(I i) {
        return iMap.add(i);
    }

}
