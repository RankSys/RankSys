/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.diversity.binom.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.diversity.binom.reranking.BinomialCoverageReranker.BinomialCoverageUserReranker;
import es.uam.eps.ir.ranksys.diversity.binom.reranking.BinomialNonRedundancyReranker.BinomialNonRedundancyUserReranker;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import java.util.List;

/**
 *
 * @author saul
 */
public class BinomialDiversityReranker<U, I, F> extends LambdaReranker<U, I> {

    private final BinomialCoverageReranker<U, I, F> coverageReranker;
    private final BinomialNonRedundancyReranker<U, I, F> nonRedundancyReranker;
    
    public BinomialDiversityReranker(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double alpha, double lambda, int cutoff) {
        super(lambda, cutoff, true);
        coverageReranker = new BinomialCoverageReranker<>(featureData, binomialModel, alpha, lambda, cutoff);
        nonRedundancyReranker = new BinomialNonRedundancyReranker<>(featureData, binomialModel, alpha, lambda, cutoff);
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new BinomialDiversityUserReranker(recommendation);
    }

    protected class BinomialDiversityUserReranker extends LambdaUserReranker {

        private final BinomialCoverageUserReranker coverageUserReranker;
        private final BinomialNonRedundancyUserReranker nonRedundancyUserReranker;
        
        public BinomialDiversityUserReranker(Recommendation<U, I> recommendation) {
            super(recommendation);
            this.coverageUserReranker = (BinomialCoverageUserReranker) coverageReranker.getUserReranker(recommendation);
            this.nonRedundancyUserReranker = (BinomialNonRedundancyUserReranker) nonRedundancyReranker.getUserReranker(recommendation);
        }

        @Override
        protected double nov(U user, IdDoublePair<I> itemValue, List<IdDoublePair<I>> reranked) {
            return coverageUserReranker.nov(user, itemValue, reranked) * nonRedundancyUserReranker.nov(user, itemValue, reranked);
        }

        @Override
        protected void update(U user, IdDoublePair<I> bestItemValue) {
            coverageUserReranker.update(user, bestItemValue);
            nonRedundancyUserReranker.update(user, bestItemValue);
        }

    }

}
