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
package es.uam.eps.ir.ranksys.diversity.intentaware.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class XQuAD<U, I, F> extends LambdaReranker<U, I> {

    private final IntentModel<U, I, F> intentModel;

    public XQuAD(IntentModel<U, I, F> intentModel, double lambda, int cutoff, boolean norm) {
        super(lambda, cutoff, norm);
        this.intentModel = intentModel;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new UserXQuAD(recommendation);
    }

    protected class UserXQuAD extends LambdaUserReranker {

        private final IntentModel<U, I, F>.UserIntentModel uim;
        private final TObjectDoubleMap<F> redundancy;
        private final TObjectDoubleMap<F> probNorm;

        public UserXQuAD(Recommendation<U, I> recommendation) {
            super(recommendation);

            this.uim = intentModel.getUserModel(recommendation.getUser());
            this.redundancy = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 1.0);
            this.probNorm = new TObjectDoubleHashMap<>();
            recommendation.getItems().forEach(i -> {
                uim.getItemIntents(i.id).sequential().forEach(f -> {
                    probNorm.adjustOrPutValue(f, i.v, i.v);
                });
            });
        }

        private double pif(IdDoublePair<I> iv, F f) {
            return iv.v / probNorm.get(f);
        }
        
        @Override
        protected double nov(IdDoublePair<I> iv) {
            return uim.getItemIntents(iv.id)
                    .mapToDouble(f -> {
                        return uim.p(f) * pif(iv, f) * redundancy.get(f);
                    }).sum();
        }

        @Override
        protected void update(IdDoublePair<I> biv) {
            uim.getItemIntents(biv.id).sequential()
                    .forEach(f -> {
                        redundancy.put(f, redundancy.get(f) * (1 - pif(biv, f)));
                    });
        }

    }

}
