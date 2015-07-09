/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Default feature-based intent-aware model. Features of the items in the user
 * profiles are used as proxies for intents, and the probability of each is
 * proportional to its occurrence in the profiles.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class FeatureIntentModel<U, I, F> extends IntentModel<U, I, F> {

    protected final PreferenceData<U, I, ?> totalData;
    protected final FeatureData<I, F, ?> featureData;

    /**
     * Constructor that caches user intent-aware models.
     *
     * @param targetUsers user whose intent-aware models are cached
     * @param totalData preference data
     * @param featureData feature data
     */
    public FeatureIntentModel(Stream<U> targetUsers, PreferenceData<U, I, ?> totalData, FeatureData<I, F, ?> featureData) {
        super(targetUsers);
        this.totalData = totalData;
        this.featureData = featureData;
    }

    /**
     * Constructor that does not cache user intent-aware models.
     *
     * @param totalData preference data
     * @param featureData feature data
     */
    public FeatureIntentModel(PreferenceData<U, I, ?> totalData, FeatureData<I, F, ?> featureData) {
        super();
        this.totalData = totalData;
        this.featureData = featureData;
    }

    @Override
    protected UserIntentModel<U, I, F> get(U user) {
        return new FeatureUserIntentModel(user);
    }

    /**
     * Default user intent-aware model for {@link FeatureIntentModel}.
     */
    public class FeatureUserIntentModel implements UserIntentModel<U, I, F> {

        private final Object2DoubleOpenHashMap<F> prob;

        /**
         * Constructor.
         *
         * @param user user whose model is created.
         */
        public FeatureUserIntentModel(U user) {
            Object2DoubleOpenHashMap<F> auxProb = new Object2DoubleOpenHashMap<>();
            auxProb.defaultReturnValue(0.0);

            int[] norm = {0};
            totalData.getUserPreferences(user).forEach(iv -> {
                featureData.getItemFeatures(iv.id).forEach(fv -> {
                    auxProb.addTo(fv.id, 1.0);
                    norm[0]++;
                });
            });

            if (norm[0] == 0) {
                norm[0] = featureData.numFeatures();
                featureData.getAllFeatures().sequential().forEach(f -> auxProb.put(f, 1.0));
            }

            auxProb.object2DoubleEntrySet().forEach(e -> {
                e.setValue(e.getDoubleValue() / norm[0]);
            });

            this.prob = auxProb;
        }

        @Override
        public Set<F> getIntents() {
            return prob.keySet();
        }

        @Override
        public Stream<F> getItemIntents(I i) {
            return featureData.getItemFeatures(i).map(fv -> fv.id).filter(getIntents()::contains);
        }

        @Override
        public double p(F f) {
            return prob.getDouble(f);
        }

    }
}
