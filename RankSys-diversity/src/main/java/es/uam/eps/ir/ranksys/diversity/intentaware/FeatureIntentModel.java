/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import java.util.Set;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;

/**
 * Default feature-based intent-aware model. Features of the items in the user profiles are used as proxies for intents, and the probability of each is proportional to its occurrence in the profiles.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class FeatureIntentModel<U, I, F> extends IntentModel<U, I, F> {

    /**
     * user-item preference data
     */
    protected final PreferenceData<U, I> totalData;

    /**
     * item features data
     */
    protected final FeatureData<I, F, ?> featureData;

    /**
     * features norms
     */
    protected Object2DoubleOpenHashMap<F> featureNorms;

    /**
     * Constructor that caches user intent-aware models.
     *
     * @param targetUsers user whose intent-aware models are cached
     * @param totalData preference data
     * @param featureData feature data
     */
    public FeatureIntentModel(Stream<U> targetUsers, PreferenceData<U, I> totalData, FeatureData<I, F, ?> featureData) {
        super(targetUsers);
        this.totalData = totalData;
        this.featureData = featureData;
        init();
    }

    /**
     * Constructor that does not cache user intent-aware models.
     *
     * @param totalData preference data
     * @param featureData feature data
     */
    public FeatureIntentModel(PreferenceData<U, I> totalData, FeatureData<I, F, ?> featureData) {
        super();
        this.totalData = totalData;
        this.featureData = featureData;
        init();
    }

    private void init() {
        featureNorms = new Object2DoubleOpenHashMap<>();
        featureData.getAllFeatures().forEach(f -> {
            int count = featureData.getFeatureItems(f)
                    .map(Tuple2::v1)
                    .mapToInt(totalData::numUsers)
                    .sum();
            featureNorms.put(f, count);
        });
    }

    /**
     * {@inheritDoc}
     *
     * @param user target user
     * @return intent model for user
     */
    @Override
    protected UserIntentModel<U, I, F> get(U user) {
        return new FeatureUserIntentModel(user);
    }

    /**
     * Default user intent-aware model for {@link FeatureIntentModel}.
     */
    public class FeatureUserIntentModel implements UserIntentModel<U, I, F> {

        /**
         * Map feature to p(f|u)
         */
        protected final Object2DoubleOpenHashMap<F> pfu;

        /**
         * Map feature to p(u|f)
         */
        protected final Object2DoubleOpenHashMap<F> puf;

        /**
         * Constructor.
         *
         * @param user user whose model is created.
         */
        public FeatureUserIntentModel(U user) {
            Object2DoubleOpenHashMap<F> tmpCounts = new Object2DoubleOpenHashMap<>();
            tmpCounts.defaultReturnValue(0.0);

            int[] norm = {0};
            totalData.getUserPreferences(user).forEach(iv -> {
                featureData.getItemFeatures(iv.v1).forEach(fv -> {
                    tmpCounts.addTo(fv.v1, 1.0);
                    norm[0]++;
                });
            });

            if (norm[0] == 0) {
                norm[0] = featureData.numFeatures();
                featureData.getAllFeatures().sequential().forEach(f -> tmpCounts.put(f, 1.0));
            }

            puf = new Object2DoubleOpenHashMap<>();
            pfu = new Object2DoubleOpenHashMap<>();
            tmpCounts.object2DoubleEntrySet().forEach(e -> {
                F f = e.getKey();
                puf.put(f, e.getDoubleValue() / featureNorms.getDouble(f));
                pfu.put(f, e.getDoubleValue() / norm[0]);
            });
        }

        /**
         * {@inheritDoc}
         *
         * @return set of features as intents
         */
        @Override
        public Set<F> getIntents() {
            return pfu.keySet();
        }

        /**
         * {@inheritDoc}
         *
         * @param i target item
         * @return features as items covered by the item
         */
        @Override
        public Stream<F> getItemIntents(I i) {
            return featureData.getItemFeatures(i)
                    .map(Tuple2::v1)
                    .filter(getIntents()::contains);
        }

        /**
         * {@inheritDoc}
         *
         * @param f feature as intent
         * @return probability of the feature-intent
         */
        @Override
        public double pf_u(F f) {
            return pfu.getDouble(f);
        }

        /**
         * {@inheritDoc}
         *
         * @param f features as intent
         * @return probability of user given an intent
         */
        @Override
        public double pu_f(F f) {
            return puf.getDouble(f);
        }
    }
}
