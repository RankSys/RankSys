/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.binom.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author saul
 */
public class BinomialNonRedundancyReranker<U, I, F> extends LambdaReranker<U, I> {

    private final FeatureData<I, F, ?> featureData;
    private final BinomialModel<U, I, F> binomialModel;
    private final double alpha;

    public BinomialNonRedundancyReranker(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double alpha, double lambda, int cutoff) {
        super(lambda, cutoff, true);
        this.featureData = featureData;
        this.binomialModel = binomialModel;
        this.alpha = alpha;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new BinomialNonRedundancyUserReranker(recommendation);
    }

    protected class BinomialNonRedundancyUserReranker extends LambdaUserReranker {

        private final BinomialModel<U, I, F>.UserBinomialModel ubm;
        private final TObjectIntMap<F> featureCount;
        private final TObjectDoubleMap<F> patienceNow;
        private final TObjectDoubleMap<F> patienceLater;

        public BinomialNonRedundancyUserReranker(Recommendation<U, I> recommendation) {
            super(recommendation);

            ubm = binomialModel.getUserModel(recommendation.getUser(), alpha);

            featureCount = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);

            patienceNow = new TObjectDoubleHashMap<>();
            patienceLater = new TObjectDoubleHashMap<>();
            ubm.getFeatures().forEach(f -> {
                patienceNow.put(f, ubm.patience(0, f, cutoff));
                patienceLater.put(f, ubm.patience(1, f, cutoff));
            });
        }

        @Override
        protected double nov(U user, IdDoublePair<I> itemValue, List<IdDoublePair<I>> reranked) {
            Set<F> itemFeatures = featureData.getItemFeatures(itemValue.id)
                    .map(fv -> fv.id)
                    .collect(Collectors.toCollection(() -> new HashSet<>()));

            double iNonRed = featureCount.keySet().stream()
                    .mapToDouble(f -> {
                        if (itemFeatures.contains(f)) {
                            return patienceLater.get(f);
                        } else {
                            return patienceNow.get(f);
                        }
                    }).reduce((x, y) -> x * y).orElse(1.0);
            int m = featureCount.size() + (int) itemFeatures.stream()
                    .filter(f -> !featureCount.containsKey(f))
                    .count();
            iNonRed = Math.pow(iNonRed, 1 / (double) m);

            return iNonRed;
        }

        @Override
        protected void update(U user, IdDoublePair<I> bestItemValue) {
            featureData.getItemFeatures(bestItemValue.id)
                    .map(fv -> fv.id)
                    .forEach(f -> {
                        int c = featureCount.adjustOrPutValue(f, 1, 1);
                        patienceNow.put(f, patienceLater.get(f));
                        patienceLater.put(f, ubm.patience(c + 1, f, cutoff));
                    });
        }

    }

}
