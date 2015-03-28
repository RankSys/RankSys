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
package es.uam.eps.ir.ranksys.diversity.sales.metrics;

import es.uam.eps.ir.ranksys.metrics.rank.NoDiscountModel;
import es.uam.eps.ir.ranksys.metrics.rel.NoRelevanceModel;

/**
 * Gini-Simpson index sales diversity metric. It is actually a relevance and rank-unaware version of {@link EIUDC} with a proportional mapping.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender Systems. PhD Thesis.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class GiniSimpsonIndex<U, I> extends EIUDC<U, I> {

    /**
     * Constructor.
     *
     * @param cutoff maximum length of the recommendation lists that is evaluated
     */
    public GiniSimpsonIndex(int cutoff) {
        super(cutoff, new NoDiscountModel(), new NoRelevanceModel<>());
    }

    @Override
    public double evaluate() {
        return (super.evaluate() - 1) / cutoff + 1;
    }

}
