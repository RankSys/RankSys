/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.pair.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.pair.ItemDistanceModel;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.List;

/**
 *
 * @author saul
 */
public class AvgMMR<U, I> extends LambdaReranker<U, I> {

    private final ItemDistanceModel<I> dist;
    
    public AvgMMR(double lambda, int cutoff, ItemDistanceModel<I> dist) {
        super(lambda, cutoff, true);
        
        this.dist = dist;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new AvgUserMMR(recommendation);
    }

    public class AvgUserMMR extends LambdaUserReranker {

        private final TObjectDoubleMap<I> avgDist;
        private int n;
        
        public AvgUserMMR(Recommendation<U, I> recommendation) {
            super(recommendation);
            
            n = 0;
            avgDist = new TObjectDoubleHashMap<>();
            recommendation.getItems().stream().sequential()
                    .map(iv -> iv.id)
                    .forEach(i -> avgDist.put(i, 0.0));
        }

        @Override
        protected double nov(U user, IdDoublePair<I> itemValue, List<IdDoublePair<I>> reranked) {
            if (reranked.isEmpty()) {
                return 0.0;
            } else {
                return avgDist.get(itemValue.id);
            }
        }

        @Override
        protected void update(U user, IdDoublePair<I> bestItemValue) {
            I bestItem = bestItemValue.id;
            avgDist.remove(bestItem);
            
            n++;
            avgDist.transformValues(d -> ((n - 1) / (double) n) * d);
            avgDist.forEachEntry((i, d) -> {
                double d2 = dist.dist(i, bestItem) / n;
                avgDist.adjustOrPutValue(i, d2, d2);
                return true;
            });
        }

    }
}
