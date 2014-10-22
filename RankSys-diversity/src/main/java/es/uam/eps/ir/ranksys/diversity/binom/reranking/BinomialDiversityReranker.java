/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.diversity.binom.reranking;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.diversity.binom.reranking.BinomialCoverageReranker.BinomialCoverageUserReranker;
import es.uam.eps.ir.ranksys.diversity.binom.reranking.BinomialNonRedundancyReranker.BinomialNonRedundancyUserReranker;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class BinomialDiversityReranker<U, I, F> extends LambdaReranker<U, I> {

    private final BinomialCoverageReranker<U, I, F> coverageReranker;
    private final BinomialNonRedundancyReranker<U, I, F> nonRedundancyReranker;
    
    public BinomialDiversityReranker(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double lambda, int cutoff) {
        super(lambda, cutoff, true);
        coverageReranker = new BinomialCoverageReranker<>(featureData, binomialModel, lambda, cutoff);
        nonRedundancyReranker = new BinomialNonRedundancyReranker<>(featureData, binomialModel, lambda, cutoff);
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
