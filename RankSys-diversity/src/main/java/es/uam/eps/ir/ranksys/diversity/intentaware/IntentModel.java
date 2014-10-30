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
package es.uam.eps.ir.ranksys.diversity.intentaware;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.model.PersonalizableModel;
import es.uam.eps.ir.ranksys.core.model.PersonalizableModel.UserModel;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class IntentModel<U, I, F> extends PersonalizableModel<U> {

    private final RecommenderData<U, I, Double> totalData;
    private final FeatureData<I, F, ?> featureData;

    public IntentModel(boolean caching, Stream<U> targetUsers, RecommenderData<U, I, Double> totalData, FeatureData<I, F, ?> featureData) {
        super(caching, targetUsers);
        this.totalData = totalData;
        this.featureData = featureData;
    }

    @Override
    protected UserIntentModel get(U user) {
        return new UserIntentModel(user);
    }

    @Override
    public UserIntentModel getUserModel(U user) {
        return (UserIntentModel) super.getUserModel(user);
    }

    public class UserIntentModel implements UserModel<U> {

        private final TObjectDoubleMap<F> prob;

        public UserIntentModel(U user) {
            TObjectDoubleMap<F> auxProb = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);

            int[] norm = {0};
            totalData.getUserPreferences(user).forEach(iv -> {
                featureData.getItemFeatures(iv.id).forEach(fv -> {
                    auxProb.adjustOrPutValue(fv.id, 1.0, 1.0);
                    norm[0]++;
                });
            });

            if (norm[0] == 0) {
                norm[0] = featureData.numFeatures();
                featureData.getAllFeatures().sequential().forEach(f -> auxProb.put(f, 1.0));
            }
            auxProb.transformValues(v -> v / norm[0]);

            this.prob = auxProb;
        }

        public Set<F> getIntents() {
            return prob.keySet();
        }

        public Stream<F> getItemIntents(I i) {
            return featureData.getItemFeatures(i).map(fv -> fv.id).filter(getIntents()::contains);
        }

        public double p(F f) {
            return prob.get(f);
        }

    }
}
