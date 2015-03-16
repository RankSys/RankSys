/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
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
import es.uam.eps.ir.ranksys.core.model.UserModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.math3.distribution.BinomialDistribution;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class BinomialModel<U, I, F> extends UserModel<U> {

    private final RecommenderData<U, I, ?> recommenderData;
    private final FeatureData<I, F, ?> featureData;
    private final Object2DoubleMap<F> globalFeatureProbs;
    private final double alpha;

    public BinomialModel(boolean caching, Stream<U> targetUsers, RecommenderData<U, I, ?> recommenderData, FeatureData<I, F, ?> featureData, double alpha) {
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
        return globalFeatureProbs.getDouble(f);
    }

    @Override
    public UserBinomialModel get(U u) {
        return new UserBinomialModel(u, alpha);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserBinomialModel getModel(U u) {
        return (UserBinomialModel) super.getModel(u);
    }

    private Object2DoubleMap<F> getGlobalFeatureProbs() {
        Object2DoubleMap<F> probs = new Object2DoubleOpenHashMap<>();
        probs.defaultReturnValue(0.0);

        int n = recommenderData.numPreferences();
        featureData.getAllFeatures().sequential().forEach(f -> {
            int numPrefs = featureData.getFeatureItems(f).mapToInt(i -> recommenderData.numUsers(i.id)).sum();
            probs.put(f, numPrefs / (double) n);
        });

        return probs;
    }

    public class UserBinomialModel implements Model<U> {

        private final U user;
        private final Object2DoubleMap<F> featureProbs;

        private UserBinomialModel(U user, double alpha) {
            this.user = user;
            this.featureProbs = getUserFeatureProbs();
        }

        public Set<F> getFeatures() {
            return featureProbs.keySet();
        }

        public double p(F f) {
            return featureProbs.getDouble(f);
        }

        public double longing(F f, int N) {
            return Math.pow(1 - p(f), N);
        }

        public double patience(int k, F f, int N) {
            double pf = p(f);
            BinomialDistribution dist = new BinomialDistribution(null, N, pf);
            double p0 = Math.pow(1 - pf, N);
            return 1 - (dist.cumulativeProbability(k - 1) - p0) / (1 - p0);
        }

        private Object2DoubleMap<F> getUserFeatureProbs() {
            if (alpha == 0.0) {
                return globalFeatureProbs;
            }
            
            Object2DoubleOpenHashMap<F> probs = new Object2DoubleOpenHashMap<>();
            probs.defaultReturnValue(0.0);
            
            int n = recommenderData.numItems(user);
            recommenderData.getUserPreferences(user).forEach(pref -> {
                featureData.getItemFeatures(pref.id).forEach(feature -> {
                    probs.addTo(feature.id, 1.0);
                });
            });

            if (probs.isEmpty()) {
                return globalFeatureProbs;
            }
            
            if (alpha < 1.0) {
                globalFeatureProbs.object2DoubleEntrySet().forEach(e -> {
                    F f = e.getKey();
                    double p = e.getDoubleValue();
                    probs.put(f, alpha * probs.getDouble(f) / n + (1 - alpha) * p);
                });
            }
            
            return probs;
        }
    }
}
