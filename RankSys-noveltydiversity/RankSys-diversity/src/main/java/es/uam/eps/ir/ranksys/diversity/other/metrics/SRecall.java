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
package es.uam.eps.ir.ranksys.diversity.other.metrics;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class SRecall<U, I, F> extends AbstractRecommendationMetric<U, I> {

    private final FeatureData<I, F, ?> featureData;
    private final int cutoff;
    protected final RelevanceModel<U, I> relModel;

    public SRecall(FeatureData<I, F, ?> featureData, int cutoff, RelevanceModel<U, I> relModel) {
        this.featureData = featureData;
        this.cutoff = cutoff;
        this.relModel = relModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());

        Set<F> subtopics = new HashSet<>();
        
        int rank = 0;
        for (IdDouble<I> iv : recommendation.getItems()) {
            if (userRelModel.isRelevant(iv.id)) {
                subtopics.addAll(featureData.getItemFeatures(iv.id).map(fv -> fv.id).collect(Collectors.toList()));
            }

            rank++;
            if (rank >= cutoff) {
                break;
            }
        }

        return subtopics.size() / (double) featureData.numFeatures();
    }

}
