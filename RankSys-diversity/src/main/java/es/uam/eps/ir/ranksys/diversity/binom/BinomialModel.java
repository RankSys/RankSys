/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.binom;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.model.UserModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.jooq.lambda.tuple.Tuple2;

/**
 * Binomial genre diversity model.
 * 
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, redundancy
 * and size-awareness in genre diversity for Recommender Systems. RecSys 2014.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class BinomialModel<U, I, F> extends UserModel<U> {

    private final PreferenceData<U, I> recommenderData;
    private final FeatureData<I, F, ?> featureData;
    private final Object2DoubleMap<F> globalFeatureProbs;
    private final double alpha;

    /**
     * Constructor.
     *
     * @param caching are the user diversity models cached?
     * @param targetUsers users whose diversity models are cached.
     * @param recommenderData preference data
     * @param featureData feature data
     * @param alpha generality-personalization parameter
     */
    public BinomialModel(boolean caching, Stream<U> targetUsers, PreferenceData<U, I> recommenderData, FeatureData<I, F, ?> featureData, double alpha) {
        super(caching, targetUsers);
        this.recommenderData = recommenderData;
        this.featureData = featureData;
        this.globalFeatureProbs = getGlobalFeatureProbs();
        this.alpha = alpha;
    }

    /**
     * Returns the features considered by the model.
     *
     * @return the features considered by the model
     */
    public Set<F> getFeatures() {
        return globalFeatureProbs.keySet();
    }

    /**
     * Returns the non-personalized probability of a feature.
     *
     * @param f feature
     * @return non-personalized probability of a feature
     */
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
            int numPrefs = featureData.getFeatureItems(f)
                    .map(Tuple2::v1)
                    .mapToInt(recommenderData::numUsers)
                    .sum();
            probs.put(f, numPrefs / (double) n);
        });

        return probs;
    }

    /**
     * Binomial diversity model of a user.
     */
    public class UserBinomialModel implements Model<U> {

        private final U user;
        private final Object2DoubleMap<F> featureProbs;

        private UserBinomialModel(U user, double alpha) {
            this.user = user;
            this.featureProbs = getUserFeatureProbs();
        }

        /**
         * Returns the features considered by the user binomial model.
         *
         * @return the features considered by the user binomial model
         */
        public Set<F> getFeatures() {
            return featureProbs.keySet();
        }

        /**
         * Returns the personalized probability of a feature.
         *
         * @param f feature
         * @return the personalized probability of a feature
         */
        public double p(F f) {
            return featureProbs.getDouble(f);
        }

        /**
         * Returns the longing, i.e., how much the feature would be missed if
         * not included in a recommendation list of a given size.
         *
         * @param f feature
         * @param N recommendation list size 
         * @return longing score
         */
        public double longing(F f, int N) {
            return Math.pow(1 - p(f), N);
        }

        /**
         * Return the patience, i.e., the penalization of having a number of
         * items with the same feature in a recommendation list of a given size.
         *
         * @param k number of times the feature appears in items in the 
         * recommendation.
         * @param f feature
         * @param N recommendation list size
         * @return patience score
         */
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
                featureData.getItemFeatures(pref.v1).forEach(feature -> {
                    probs.addTo(feature.v1, 1.0);
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
