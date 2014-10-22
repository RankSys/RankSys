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
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class BinomialCoverageReranker<U, I, F> extends LambdaReranker<U, I> {

    private final FeatureData<I, F, ?> featureData;
    private final BinomialModel<U, I, F> binomialModel;

    public BinomialCoverageReranker(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double lambda, int cutoff) {
        super(lambda, cutoff, true);
        this.featureData = featureData;
        this.binomialModel = binomialModel;
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new BinomialCoverageUserReranker(recommendation);
    }

    public class BinomialCoverageUserReranker extends LambdaUserReranker {

        private final BinomialModel<U, I, F>.UserBinomialModel ubm;
        private final Set<F> uncoveredFeatures;
        private double coverage;

        public BinomialCoverageUserReranker(Recommendation<U, I> recommendation) {
            super(recommendation);

            ubm = binomialModel.getUserModel(recommendation.getUser());

            uncoveredFeatures = new HashSet<>(ubm.getFeatures());
            coverage = uncoveredFeatures.stream()
                    .mapToDouble(f -> ubm.longing(f, cutoff))
                    .reduce((x, y) -> x * y).orElse(1.0);
            coverage = Math.pow(coverage, 1 / (double) ubm.getFeatures().size());

        }

        @Override
        protected double nov(U user, IdDoublePair<I> itemValue, List<IdDoublePair<I>> reranked) {
            double iCoverage = featureData.getItemFeatures(itemValue.id)
                    .map(fv -> fv.id)
                    .filter(uncoveredFeatures::contains)
                    .mapToDouble(f -> ubm.longing(f, cutoff))
                    .reduce((x, y) -> x * y).orElse(1.0);
            iCoverage = Math.pow(iCoverage, 1 / (double) ubm.getFeatures().size());
            iCoverage = coverage / iCoverage;

            return iCoverage;
        }

        @Override
        protected void update(U user, IdDoublePair<I> bestItemValue) {
            double iCoverage = featureData.getItemFeatures(bestItemValue.id).sequential()
                    .map(fv -> fv.id)
                    .filter(uncoveredFeatures::remove)
                    .mapToDouble(f -> ubm.longing(f, cutoff))
                    .reduce((x, y) -> x * y).orElse(1.0);
            iCoverage = Math.pow(iCoverage, 1 / (double) ubm.getFeatures().size());
            coverage /= iCoverage;
        }

    }

}
