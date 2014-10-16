/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.novelty;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.diversity.pair.ItemDistanceModel;
import java.util.stream.StreamSupport;

/**
 *
 * @author saul
 */
public class PDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final RecommenderData<U, I, ?> trainData;
    private final ItemDistanceModel<I> dist;

    public PDItemNovelty(RecommenderData<U, I, ?> trainData, ItemDistanceModel<I> dist) {
        this.trainData = trainData;
        this.dist = dist;
    }

    @Override
    public double novelty(I i, U u) {
        return StreamSupport.stream(trainData.getUserPreferences(u).spliterator(), false)
                .map(jv -> jv.id)
                .mapToDouble(j -> dist.dist(i, j))
                .filter(v -> !Double.isNaN(v))
                .average().orElse(0.0);
    }

}
