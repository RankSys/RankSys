/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.intent.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.intent.IntentModel;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.List;

/**
 *
 * @author saul
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
        protected double nov(U user, IdDoublePair<I> iv, List<IdDoublePair<I>> reranked) {
            return uim.getItemIntents(iv.id)
                    .mapToDouble(f -> {
                        return uim.p(f) * pif(iv, f) * redundancy.get(f);
                    }).sum();
        }

        @Override
        protected void update(U user, IdDoublePair<I> biv) {
            uim.getItemIntents(biv.id).sequential()
                    .forEach(f -> {
                        redundancy.put(f, redundancy.get(f) * (1 - pif(biv, f)));
                    });
        }

    }

}
