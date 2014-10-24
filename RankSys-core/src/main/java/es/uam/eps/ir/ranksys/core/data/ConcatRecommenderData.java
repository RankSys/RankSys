/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.data;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class ConcatRecommenderData<U, I, V> implements RecommenderData<U, I, V> {

    private final RecommenderData<U, I, V> d1;
    private final RecommenderData<U, I, V> d2;

    public ConcatRecommenderData(RecommenderData<U, I, V> d1, RecommenderData<U, I, V> d2) {
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public int numUsers() {
        return (int) getAllUsers().count();
    }

    @Override
    public int numUsers(I i) {
        return d1.numUsers(i) + d2.numUsers(i);
    }

    @Override
    public int numItems() {
        return (int) getAllItems().count();
    }

    @Override
    public int numItems(U u) {
        return d1.numItems(u) + d2.numItems(u);
    }

    @Override
    public int numPreferences() {
        return d1.numPreferences() + d2.numPreferences();
    }

    @Override
    public Stream<U> getAllUsers() {
        return Stream.concat(d1.getAllUsers(), d2.getAllUsers()).distinct();
    }

    @Override
    public Stream<I> getAllItems() {
        return Stream.concat(d1.getAllItems(), d2.getAllItems()).distinct();
    }

    @Override
    public Stream<IdValuePair<I, V>> getUserPreferences(U u) {
        return Stream.concat(d1.getUserPreferences(u), d2.getUserPreferences(u));
    }

    @Override
    public Stream<IdValuePair<U, V>> getItemPreferences(I i) {
        return Stream.concat(d1.getItemPreferences(i), d2.getItemPreferences(i));
    }
}
