/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.diversity.binom;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.model.PersonalizableModel;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.math3.distribution.BinomialDistribution;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class BinomialModel<U, I, F> extends PersonalizableModel<U> {

    private final RecommenderData<U, I, Double> recommenderData;
    private final FeatureData<I, F, ?> featureData;
    private final TObjectDoubleMap<F> globalFeatureProbs;
    private final double alpha;

    public BinomialModel(boolean caching, Stream<U> targetUsers, RecommenderData<U, I, Double> recommenderData, FeatureData<I, F, ?> featureData, double alpha) {
        super(caching, targetUsers);
        this.recommenderData = recommenderData;
        this.featureData = featureData;
        this.globalFeatureProbs = getGlobalFeatureProbs();
        this.alpha = alpha;
    }

    public Set<F> getFeatures() {
        return globalFeatureProbs.keySet();
    }

    public double p(F f) {
        return globalFeatureProbs.get(f);
    }

    @Override
    public UserBinomialModel get(U u) {
        return new UserBinomialModel(u, alpha);
    }

    @Override
    public UserBinomialModel getUserModel(U u) {
        return (UserBinomialModel) super.getUserModel(u);
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

    public class UserBinomialModel implements UserModel<U> {

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
            recommenderData.getUserPreferences(user).forEach(pref -> {
                featureData.getItemFeatures(pref.id).forEach(feature -> {
                    probs.adjustOrPutValue(feature.id, 1.0, 1.0);
                });
            });

            probs.transformValues(p -> p / n);

            return probs;
        }
    }
}
