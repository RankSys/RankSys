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
package es.uam.eps.ir.ranksys.metrics.rel;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Relevance model in which the items in a preference subset with a value
 * equal or above a threshold are judged as relevant.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class BinaryRelevanceModel<U, I> extends IdealRelevanceModel<U, I> {

    private final PreferenceData<U, I> testData;
    private final double threshold;

    /**
     * Constructor
     *
     * @param caching are the user relevance models being cached?
     * @param testData test subset of the preferences
     * @param threshold relevance threshold
     */
    public BinaryRelevanceModel(boolean caching, PreferenceData<U, I> testData, double threshold) {
        super(caching, testData.getUsersWithPreferences());
        this.testData = testData;
        this.threshold = threshold;
    }

    @Override
    protected UserIdealRelevanceModel<U, I> get(U user) {
        return new UserBinaryRelevanceModel(user);
    }

    private class UserBinaryRelevanceModel implements UserIdealRelevanceModel<U, I> {

        private final Set<I> relevantItems;

        public UserBinaryRelevanceModel(U user) {
            this.relevantItems = testData.getUserPreferences(user)
                    .filter(iv -> iv.v >= threshold)
                    .map(iv -> iv.id)
                    .collect(Collectors.toSet());
        }

        @Override
        public Set<I> getRelevantItems() {
            return relevantItems;
        }

        @Override
        public boolean isRelevant(I item) {
            return relevantItems.contains(item);
        }

        @Override
        public double gain(I item) {
            return isRelevant(item) ? 1.0 : 0.0;
        }

    }
}
