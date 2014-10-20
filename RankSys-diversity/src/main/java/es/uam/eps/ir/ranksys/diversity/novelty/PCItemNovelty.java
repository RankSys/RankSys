/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.novelty;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

/**
 *
 * @author saul
 */
public class PCItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final TObjectDoubleMap<I> itemNovelty;

    public PCItemNovelty(RecommenderData<U, I, ?> recommenderData) {
        itemNovelty = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 1.0);
        int numUsers = recommenderData.numUsers();
        recommenderData.getAllItems().forEach(i -> {
            itemNovelty.put(i, 1 - recommenderData.numUsers(i) / (double) numUsers);
        });
    }

    @Override
    public double novelty(I i, U u) {
        return itemNovelty.get(i);
    }

}
