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
package es.uam.eps.ir.ranksys.metrics.rel;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class BinaryRelevanceModel<U, I> extends IdealRelevanceModel<U, I> {

    private final RecommenderData<U, I, ?> testData;
    private final double threshold;

    public BinaryRelevanceModel(boolean caching, RecommenderData<U, I, ?> testData, double threshold) {
        super(caching, testData.getAllUsers());
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
