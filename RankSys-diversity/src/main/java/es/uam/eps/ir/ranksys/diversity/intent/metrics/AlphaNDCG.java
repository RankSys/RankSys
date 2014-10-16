/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.intent.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 *
 * @author saul
 */
public class AlphaNDCG<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final double alpha;
    private final RelevanceModel<U, I> relModel;
    private final FeatureData<I, F, ?> featureData;
    private final TObjectDoubleMap<U> idcgMap;

    public AlphaNDCG(int cutoff, double alpha, FeatureData<I, F, ?> featureData, RecommenderData<U, I, Double> testData, double threshold) {
        super();
        this.cutoff = cutoff;
        this.alpha = alpha;
        this.relModel = new BinaryRelevanceModel<>(testData, threshold);
        this.featureData = featureData;
        
        this.idcgMap = StreamSupport.stream(testData.getAllUsers().spliterator(), true)
                .map(u -> {
                    return new IdDoublePair<U>(u, idcg(relModel.getUserModel(u)));
                })
                .collect(
                        () -> new TObjectDoubleHashMap<>(),
                        (m, iv) -> m.put(iv.id, iv.v),
                        (m1, m2) -> m1.putAll(m2)
                );
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        UserRelevanceModel<U, I> urm = relModel.getUserModel(recommendation.getUser());

        double ndcg = 0.0;
        int rank = 0;
        TObjectIntMap<F> redundancy = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);

        for (IdDoublePair<I> pair : recommendation.getItems()) {
            if (urm.isRelevant(pair.id)) {
                double gain = featureData.getItemFeatures(pair.id).sequential()
                        .map(fv -> fv.id)
                        .mapToDouble(f -> {
                            int r = redundancy.adjustOrPutValue(f, 1, 1) - 1;
                            return Math.pow(1 - alpha, r);
                        }).sum();
                ndcg += gain / Math.log(rank + 2) * Math.log(2);
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        if (ndcg > 0) {
            ndcg /= idcgMap.get(recommendation.getUser());
        }

        return ndcg;
    }

    private double idcg(UserRelevanceModel<U, I> urm) {
        double idcg = 0;

        TObjectIntMap<F> redundancy = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);
        Set<I> candidates = urm.getRelevantItems();
        int rank = 0;

        while (rank <= cutoff && !candidates.isEmpty()) {
            I bi = null;
            double bg = Double.NEGATIVE_INFINITY;
            for (I i : candidates) {
                double gain = featureData.getItemFeatures(i)
                        .map(fv -> fv.id)
                        .mapToDouble(f -> {
                            return Math.pow(1 - alpha, redundancy.get(f));
                        }).sum();
                if (gain > bg) {
                    bg = gain;
                    bi = i;
                }
            }
            candidates.remove(bi);
            featureData.getItemFeatures(bi).sequential()
                    .map(fv -> fv.id)
                    .forEach(f -> redundancy.adjustOrPutValue(f, 1, 1));
            idcg += bg / Math.log(rank + 2) * Math.log(2);
            rank++;
        }

        return idcg;
    }
}
