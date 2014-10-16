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
import static java.lang.Math.log;
import static java.lang.Math.min;

/**
 *
 * @author saul
 */
public class FDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final TObjectDoubleMap<I> itemNovelty;

    public FDItemNovelty(RecommenderData<U, I, ?> recommenderData) {
        int norm = 0;
        double maxNov = Double.POSITIVE_INFINITY;
        for (I i : recommenderData.getAllItems()) {
            int n = recommenderData.numUsers(i);
            norm += n;
            maxNov = min(maxNov, n);
        }
        maxNov = -log(maxNov / norm) / log(2);
        itemNovelty = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, maxNov);
        for (I i : recommenderData.getAllItems()) {
            itemNovelty.put(i, -log(recommenderData.numUsers(i) / (double) norm) / log(2));
        }
    }

    @Override
    public double novelty(I i, U u) {
        return itemNovelty.get(i);
    }

}
