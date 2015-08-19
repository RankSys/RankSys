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
package es.uam.eps.ir.ranksys.novdiv.reranking;

import es.uam.eps.ir.ranksys.core.Recommendation;
import static java.lang.Math.min;

/**
 * Null re-ranker that does not perform an actual re-ranking.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class NullReranker<U, I> extends PermutationReranker<U, I> {

    /**
     * Returns the permutation that is applied to the input recommendation
     * to generate the re-ranked recommendation.
     *
     * @param recommendation input recommendation
     * @param maxLength maximum length of the permutation
     * @return permutation that encodes the re-ranking
     */
    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation, int maxLength) {
        return getBasePerm(min(maxLength, recommendation.getItems().size()));
    }

}
