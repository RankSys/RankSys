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

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class BinomialMetric<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final BinomialModel<U, I, F> binomialModel;
    private final FeatureData<I, F, ?> featureData;
    private final int cutoff;
    protected final RelevanceModel<U, I> relModel;

    public BinomialMetric(BinomialModel<U, I, F> binomialModel, FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        this.binomialModel = binomialModel;
        this.featureData = featureData;
        this.cutoff = cutoff;
        this.relModel = relModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());
        BinomialModel<U, I, F>.UserBinomialModel prob = binomialModel.getModel(recommendation.getUser());

        Object2IntOpenHashMap<F> count = new Object2IntOpenHashMap<>();
        count.defaultReturnValue(0);

        int rank = 0;
        int nrel = 0;
        for (IdDouble<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.id)) {
                featureData.getItemFeatures(iv.id).forEach(fv -> {
                    count.addTo(fv.id, 1);
                });
                nrel++;
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        return getResultFromCount(prob, count, nrel, rank);
    }

    protected abstract double getResultFromCount(BinomialModel<U, I, F>.UserBinomialModel prob, Object2IntMap<F> count, int nrel, int nret);

}
