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
package es.uam.eps.ir.ranksys.diversity.itemnovelty.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.metrics.AbstractRecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public abstract class ItemNoveltyMetric<U, I> extends AbstractRecommendationMetric<U, I> {

    private final int cutoff;
    private final ItemNovelty<U, I> novelty;
    private final RelevanceModel<U, I> relModel;

    public ItemNoveltyMetric(int cutoff, ItemNovelty<U, I> novelty, RelevanceModel<U, I> relevanceModel) {
        super();
        this.cutoff = cutoff;
        this.novelty = novelty;
        this.relModel = relevanceModel;
    }

    @Override
    public double evaluate(Recommendation<U, I> recommendation) {
        U u = recommendation.getUser();
        RelevanceModel.UserRelevanceModel<U, I> userRelModel = relModel.getUserModel(u);
        
        double nov = 0;

        int rank = 0;
        for (IdDoublePair<I> iv : recommendation.getItems()) {
            nov += userRelModel.gain(iv.id) * novelty.novelty(iv.id, u);
            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
        nov /= rank;

        return nov;
    }

}
