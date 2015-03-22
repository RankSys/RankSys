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

import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;

/**
 * Precision metric: proportion of relevant items in a recommendation list.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class Precision<U, I> extends AbstractRecommendationMetric<U, I> {

    private final RelevanceModel<U, I> relModel;
    private final int cutoff;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of recommended lists
     * @param relModel relevance model
     */
    public Precision(int cutoff, BinaryRelevanceModel<U, I> relModel) {
        this.relModel = relModel;
        this.cutoff = cutoff;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        UserRelevanceModel<U, I> userRelModel = relModel.getModel(recommendation.getUser());
        
        return recommendation.getItems().stream()
                .limit(cutoff)
                .filter(is -> userRelModel.isRelevant(is.id))
                .count() / (double) cutoff;
    }
}
