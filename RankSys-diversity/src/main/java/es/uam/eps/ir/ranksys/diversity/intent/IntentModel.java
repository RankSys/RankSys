/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.intent;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author saul
 */
public class IntentModel<U, I, F> {

    private final RecommenderData<U, I, Double> totalData;
    private final FeatureData<I, F, ?> featureData;
    private final Map<U, UserIntentModel> userMap;

    public IntentModel(RecommenderData<U, I, Double> totalData, Iterable<U> targetUsers, FeatureData<I, F, ?> featureData) {
        this.totalData = totalData;
        this.featureData = featureData;
        userMap = StreamSupport.stream(targetUsers.spliterator(), true)
                .map(user -> new UserIntentModel(user))
                .collect(Collectors.toMap(uim -> uim.user, uim -> uim));
    }

    public UserIntentModel getUserModel(U user) {
        return userMap.get(user);
    }

    public class UserIntentModel {

        private final U user;
        private final TObjectDoubleMap<F> prob;

        public UserIntentModel(U user) {
            this.user = user;
            TObjectDoubleMap<F> auxProb = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0.0);

            int[] norm = {0};
            for (IdValuePair<I, Double> iv : totalData.getUserPreferences(user)) {
                featureData.getItemFeatures(iv.id).forEach(fv -> {
                    auxProb.adjustOrPutValue(fv.id, 1.0, 1.0);
                    norm[0]++;
                });
            }

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
