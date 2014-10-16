/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.binom;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.Set;
import org.apache.commons.math3.distribution.BinomialDistribution;

/**
 *
 * @author saul
 */
public class BinomialModel<U, I, F> {

    private final RecommenderData<U, I, Double> recommenderData;
    private final FeatureData<I, F, ?> featureData;
    private final TObjectDoubleMap<F> globalFeatureProbs;

    public BinomialModel(RecommenderData<U, I, Double> recommenderData, FeatureData<I, F, ?> featureData) {
        this.recommenderData = recommenderData;
        this.featureData = featureData;
        this.globalFeatureProbs = getGlobalFeatureProbs();
    }

    public Set<F> getFeatures() {
        return globalFeatureProbs.keySet();
    }

    public double p(F f) {
        return globalFeatureProbs.get(f);
    }

    public UserBinomialModel getUserModel(U u, double alpha) {
        return new UserBinomialModel(u, alpha);
    }

    private TObjectDoubleMap<F> getGlobalFeatureProbs() {
        TObjectDoubleMap<F> probs = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);

        int n = recommenderData.numPreferences();
        featureData.getAllFeatures().sequential().forEach(f -> {
            int numPrefs = featureData.getFeatureItems(f).mapToInt(i -> recommenderData.numUsers(i.id)).sum();
            probs.put(f, numPrefs);
        });
        probs.transformValues(v -> v / n);

        return probs;
    }

    public class UserBinomialModel {

        private final TObjectDoubleMap<F> userFeatureProbs;
        private final double alpha;

        private UserBinomialModel(U user, double alpha) {
            this.alpha = alpha;

            this.userFeatureProbs = getUserFeatureProbs(user);
        }

        public Set<F> getFeatures() {
            if (alpha < 1.0) {
                return globalFeatureProbs.keySet();
            } else {
                return userFeatureProbs.keySet();
            }
        }

        public double p(F f) {
            return alpha * userFeatureProbs.get(f) + (1 - alpha) * globalFeatureProbs.get(f);
        }

        public double longing(F f, int N) {
            return Math.pow(1 - p(f), N);
        }

        public double patience(int k, F f, int N) {
            BinomialDistribution dist = new BinomialDistribution(null, N, p(f));
            return 1 - (dist.cumulativeProbability(k - 1) - dist.probability(0)) / (1 - dist.probability(0));
        }

        private TObjectDoubleMap<F> getUserFeatureProbs(U user) {
            TObjectDoubleMap<F> probs = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);

            int n = recommenderData.numItems(user);
            for (IdValuePair<I, Double> pref : recommenderData.getUserPreferences(user)) {
                featureData.getItemFeatures(pref.id).forEach(feature -> {
                    probs.adjustOrPutValue(feature.id, 1.0, 1.0);
                });
            }

            probs.transformValues(p -> p / n);

            return probs;
        }
    }
}
