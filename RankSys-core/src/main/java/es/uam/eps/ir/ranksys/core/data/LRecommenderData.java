/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.data;

import es.uam.eps.ir.ranksys.core.IdxValuePair;
import es.uam.eps.ir.ranksys.core.IndexedUser;
import es.uam.eps.ir.ranksys.core.IndexedItem;
import java.util.stream.IntStream;

/**
 *
 * @author saul
 */
public interface LRecommenderData<U, I, V> extends RecommenderData<U, I, V>, IndexedItem<U>, IndexedUser<I> {

    public int numUsers(int iidx);

    public int numItems(int uidx);
    
    public IntStream getAllUidx();
    
    public IntStream getAllIidx();

    public Iterable<IdxValuePair<V>> getUidxPreferences(int uidx);

    public Iterable<IdxValuePair<V>> getIidxPreferences(int iidx);
}
