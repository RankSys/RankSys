/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.diversity.aggregate.metrics;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.metrics.AbstractSystemMetric;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class AggregateDiversityMetric<U, I> extends AbstractSystemMetric<U, I> {

    private final RelevanceModel<U, I> relModel;
    private final Set<I> recommendedItems;
    private final int cutoff;

    public AggregateDiversityMetric(int cutoff, RelevanceModel<U, I> relModel) {
        this.relModel = relModel;
        this.recommendedItems = new HashSet<>();
        this.cutoff = cutoff;
    }

    @Override
    public void add(Recommendation<U, I> recommendation) {
        U u = recommendation.getUser();
        UserRelevanceModel<U, I> urm = relModel.getUserModel(u);
        
        int rank = 0;
        for (IdDoublePair<I> ivp : recommendation.getItems()) {
            if (urm.isRelevant(ivp.id)) {
                recommendedItems.add(ivp.id);
            }
            rank++;
            if (rank >= cutoff) {
                break;
            }
        }
    }

    @Override
    public double evaluate() {
        return recommendedItems.size();
    }
}
