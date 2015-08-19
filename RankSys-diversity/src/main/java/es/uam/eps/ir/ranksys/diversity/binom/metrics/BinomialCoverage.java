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
package es.uam.eps.ir.ranksys.diversity.binom.metrics;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * Binomial coverage metric.
 * 
 * S. Vargas, L. Baltrunas, A. Karatzoglou, P. Castells. Coverage, redundancy
 * and size-awareness in genre diversity for Recommender Systems. RecSys 2014.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class BinomialCoverage<U, I, F> extends BinomialMetric<U, I, F> {

    /**
     * Constructor.
     *
     * @param binomialModel binomial diversity model
     * @param featureData feature data
     * @param cutoff maximum length of the recommendation list to be evaluated
     * @param relModel relevance model
     */
    public BinomialCoverage(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        super(binomialModel, featureData, cutoff, relModel);
    }

    /**
     * Returns the value of the binomial coverage metric for a given count of 
     * features from a recommendation list.
     *
     * @param ubm user binomial model
     * @param count count map of each feature in the recommendation
     * @param nrel number of relevant documents in the recommendation
     * @param nret length of the recommendation
     * @return value of the binomial coverage
     */
    @Override
    protected double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel ubm, Object2IntMap<F> count, int nrel, int nret) {
        return coverage(ubm, count, nret);
    }

    /**
     * Returns the value of the binomial coverage metric for a given count of 
     * features from a recommendation list.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param <F> type of the features
     * @param ubm user binomial model
     * @param count count map of each feature in a recommendation
     * @param nret length of the recommendation
     * @return value of the binomial coverage
     */
    protected static <U, I, F> double coverage(BinomialModel<U, I, F>.UserBinomialModel ubm, Object2IntMap<F> count, int nret) {
        double coverage = ubm.getFeatures().stream().
                filter(f -> !count.containsKey(f)).
                mapToDouble(f -> ubm.longing(f, nret)).
                reduce(1.0, (p, q) -> p * q);
        coverage = Math.pow(coverage, 1.0 / (double) ubm.getFeatures().size());
        
        return coverage;
    }
}
