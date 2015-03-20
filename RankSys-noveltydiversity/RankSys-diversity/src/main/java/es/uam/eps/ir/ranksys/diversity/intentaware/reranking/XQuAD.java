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
package es.uam.eps.ir.ranksys.diversity.intentaware.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.intentaware.IntentModel;
import es.uam.eps.ir.ranksys.novdiv.reranking.LambdaReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

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
        private final Object2DoubleOpenHashMap<F> redundancy;
        private final Object2DoubleOpenHashMap<F> probNorm;

        public UserXQuAD(Recommendation<U, I> recommendation) {
            super(recommendation);

            this.uim = intentModel.getModel(recommendation.getUser());
            this.redundancy = new Object2DoubleOpenHashMap<>();
            this.redundancy.defaultReturnValue(1.0);
            this.probNorm = new Object2DoubleOpenHashMap<>();
            recommendation.getItems().forEach(iv -> {
                uim.getItemIntents(iv.id).sequential().forEach(f -> {
                    probNorm.addTo(f, iv.v);
                });
            });
        }

        private double pif(IdDouble<I> iv, F f) {
            return iv.v / probNorm.getDouble(f);
        }
        
        @Override
        protected double nov(IdDouble<I> iv) {
            return uim.getItemIntents(iv.id)
                    .mapToDouble(f -> {
                        return uim.p(f) * pif(iv, f) * redundancy.getDouble(f);
                    }).sum();
        }

        @Override
        protected void update(IdDouble<I> biv) {
            uim.getItemIntents(biv.id).sequential()
                    .forEach(f -> {
                        redundancy.put(f, redundancy.getDouble(f) * (1 - pif(biv, f)));
                    });
        }

    }

}
