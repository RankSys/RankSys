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
package es.uam.eps.ir.ranksys.diversity.pairwise.metrics;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.pairwise.ItemDistanceModel;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class EILD<U, I> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ItemDistanceModel<I> distModel;
    private final RelevanceModel<U, I> relModel;

    public EILD(int cutoff, ItemDistanceModel<I> distModel, RelevanceModel<U, I> relModel) {
        this.cutoff = cutoff;
        this.distModel = distModel;
        this.relModel = relModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getUserModel(recommendation.getUser());

        List<I> items = recommendation.getItems().stream().map(is -> is.id).collect(Collectors.toList());
        int N = Math.min(cutoff, items.size());

        double eild = 0.0;
        double norm = 0;
        for (int i = 0; i < N; i++) {
            double ieild = 0.0;
            double inorm = 0.0;
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    continue;
                }
                double dist = distModel.dist(items.get(i), items.get(j));
                if (!Double.isNaN(dist)) {
                    ieild += userRelModel.gain(items.get(j)) * dist;
                    inorm += userRelModel.gain(items.get(j));
                }
            }
            if (inorm > 0) {
                eild += userRelModel.gain(items.get(i)) * ieild / inorm;
                norm++;
            }
        }
        if (norm > 0) {
            eild /= norm;
        }

        return eild;
    }

}
