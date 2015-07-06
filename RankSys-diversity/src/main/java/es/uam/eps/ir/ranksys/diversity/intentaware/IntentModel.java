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

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.model.UserModel;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Intent-aware model.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public abstract class IntentModel<U, I, F> extends UserModel<U> {

    protected final PreferenceData<U, I, ?> totalData;
    protected final FeatureData<I, F, ?> featureData;

    /**
     * Constructor that caches user intent-aware models.
     *
     * @param targetUsers user whose intent-aware models are cached
     * @param totalData preference data
     * @param featureData feature data
     */
    public IntentModel(Stream<U> targetUsers, PreferenceData<U, I, ?> totalData, FeatureData<I, F, ?> featureData) {
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
    public IntentModel(PreferenceData<U, I, ?> totalData, FeatureData<I, F, ?> featureData) {
        super();
        this.totalData = totalData;
        this.featureData = featureData;
    }

    @Override
    protected abstract UserIntentModel get(U user);

    @SuppressWarnings("unchecked")
    @Override
    public UserIntentModel getModel(U user) {
        return (UserIntentModel) super.getModel(user);
    }

    /**
     * User intent-aware model for {@link IntentModel}.
     */
    public abstract class UserIntentModel implements Model<U> {

        /**
         * Returns the features considered in the intent model.
         *
         * @return the features considered in the intent model
         */
        public abstract Set<F> getIntents();

        /**
         * Returns the features associated with an item.
         *
         * @param i item
         * @return the features associated with the item
         */
        public abstract Stream<F> getItemIntents(I i);

        /**
         * Returns the probability of a feature in the model.
         *
         * @param f feature
         * @return probability of a feature in the model
         */
        public abstract double p(F f);

    }
}
