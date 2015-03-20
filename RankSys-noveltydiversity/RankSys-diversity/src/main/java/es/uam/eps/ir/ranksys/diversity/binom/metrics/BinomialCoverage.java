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
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class BinomialCoverage<U, I, F> extends BinomialMetric<U, I, F> {

    public BinomialCoverage(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        super(binomialModel, featureData, cutoff, relModel);
    }

    @Override
    protected double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, Object2IntMap<F> count, int nrel, int nret) {
        return coverage(prob, count, nret);
    }

    protected static <U, I, F> double coverage(BinomialModel<U, I, F>.UserBinomialModel ubm, Object2IntMap<F> count, int nret) {
        double coverage = ubm.getFeatures().stream().
                filter(f -> !count.containsKey(f)).
                mapToDouble(f -> ubm.longing(f, nret)).
                reduce(1.0, (p, q) -> p * q);
        coverage = Math.pow(coverage, 1.0 / (double) ubm.getFeatures().size());
        
        return coverage;
    }
}
