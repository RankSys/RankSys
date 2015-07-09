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
 * @param <F> type of the intent
 */
public abstract class IntentModel<U, I, F> extends UserModel<U> {

    /**
     * Constructor that caches user intent-aware models.
     *
     * @param targetUsers user whose intent-aware models are cached
     */
    public IntentModel(Stream<U> targetUsers) {
        super(targetUsers);
    }

    /**
     * Constructor that does not cache user intent-aware models.
     */
    public IntentModel() {
        super();
    }

    @Override
    protected abstract UserIntentModel<U, I, F> get(U user);

    @SuppressWarnings("unchecked")
    @Override
    public UserIntentModel<U, I, F> getModel(U user) {
        return (UserIntentModel<U, I, F>) super.getModel(user);
    }

    /**
     * User intent-aware model for {@link IntentModel}.
     */
    public interface UserIntentModel<U, I, F> extends Model<U> {

        /**
         * Returns the intents considered in the intent model.
         *
         * @return the intents considered in the intent model
         */
        public abstract Set<F> getIntents();

        /**
         * Returns the intents associated with an item.
         *
         * @param i item
         * @return the intents associated with the item
         */
        public abstract Stream<F> getItemIntents(I i);

        /**
         * Returns the probability of an intent in the model.
         *
         * @param f intent
         * @return probability of an intent in the model
         */
        public abstract double p(F f);

    }
}
