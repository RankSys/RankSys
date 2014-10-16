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
public class MinMMR<U, I> extends LambdaReranker<U, I> {

    private final ItemDistanceModel<I> dist;
    
    public MinMMR(double lambda, int cutoff, ItemDistanceModel<I> dist) {
        super(lambda, cutoff, true);
        
        this.dist = dist;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new MinUserMMR(recommendation);
    }

    public class MinUserMMR extends LambdaUserReranker {

        private final TObjectDoubleMap<I> minDist;
        
        public MinUserMMR(Recommendation<U, I> recommendation) {
            super(recommendation);
            
            minDist = new TObjectDoubleHashMap<>();
            recommendation.getItems().stream().sequential()
                    .map(iv -> iv.id)
                    .forEach(i -> minDist.put(i, Double.POSITIVE_INFINITY));
        }

        @Override
        protected double nov(U user, IdDoublePair<I> itemValue, List<IdDoublePair<I>> reranked) {
            if (reranked.isEmpty()) {
                return 0.0;
            } else {
                return minDist.get(itemValue.id);
            }
        }

        @Override
        protected void update(U user, IdDoublePair<I> bestItemValue) {
            I bestItem = bestItemValue.id;
            minDist.remove(bestItem);
            
            minDist.forEachEntry((i, d) -> {
                minDist.put(i, Math.min(d, dist.dist(i, bestItem)));
                return true;
            });
        }

    }
}
