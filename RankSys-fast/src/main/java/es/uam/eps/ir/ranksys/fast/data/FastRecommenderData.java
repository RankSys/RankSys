/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.fast.data;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.fast.IdxPref;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author saul
 */
public interface FastRecommenderData<U, I, V> extends RecommenderData<U, I, V>, FastUserIndex<U>, FastItemIndex<I> {

    public int numUsers(int iidx);

    public int numItems(int uidx);
    
    public IntStream getAllUidx();
    
    public IntStream getAllIidx();

    public Stream<IdxPref<V>> getUidxPreferences(int uidx);

    public Stream<IdxPref<V>> getIidxPreferences(int iidx);
}
