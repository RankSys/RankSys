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

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import java.util.List;
import static java.util.stream.IntStream.range;

/**
 * Reciprocal Rank (RR, also known as MRR when averaged over queries/users).
 *
 * @param <U> type of the users
 * @param <I> type of the items
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class ReciprocalRank<U, I> extends AbstractRecommendationMetric<U, I> {

    private final RelevanceModel<U, I> relModel;
    private final int cutoff;

    /**
     * Constructor.
     *
     * @param cutoff maximum length of recommended lists
     * @param relModel relevance model
     */
    public ReciprocalRank(int cutoff, RelevanceModel<U, I> relModel) {
        this.relModel = relModel;
        this.cutoff = cutoff;
    }

    /**
     * Returns a score for the recommendation list.
     *
     * @param recommendation recommendation list
     * @return score of the metric to the recommendation
     */
    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        RelevanceModel.UserRelevanceModel<U, I> urm = relModel.getModel(recommendation.getUser());

        List<IdDouble<I>> items = recommendation.getItems();
        int r = range(0, items.size())
                .limit(cutoff)
                .filter(k -> urm.isRelevant(items.get(k).id))
                .findFirst().orElse(-1);
        
        if (r == -1) {
            return 0;
        } else {
            return 1 / (1.0 + r);
        }
    }
}
