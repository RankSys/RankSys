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
package es.uam.eps.ir.ranksys.metrics.basic;

import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.IdealRelevanceModel.UserIdealRelevanceModel;
import static java.lang.Math.min;

/**
 * Average Precision: average of the precision at each recall level.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class AveragePrecision<U, I> extends AbstractRecommendationMetric<U, I> {

    private final IdealRelevanceModel<U, I> relModel;
    private final int cutoff;

    /**
     * Constructor.
     *
     * @param cutoff cutoff of the metric
     * @param relevanceModel relevance model
     */
    public AveragePrecision(int cutoff, IdealRelevanceModel<U, I> relevanceModel) {
        super();
        this.relModel = relevanceModel;
        this.cutoff = cutoff;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        UserIdealRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());
        
        double ap = 0;
        int relCount = 0;
        int rank = 0;

        for (IdDouble<I> pair : recommendation.getItems()) {
            rank++;
            if (userRelModel.isRelevant(pair.id)) {
                relCount++;
                ap += relCount / (double) rank;
            }
            if (rank == cutoff) {
                break;
            }
        }

        return ap / (double) min(cutoff, userRelModel.getRelevantItems().size());
    }
}
