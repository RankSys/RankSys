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
package es.uam.eps.ir.ranksys.novelty.sales.reranking;

import es.uam.eps.ir.ranksys.novdiv.itemnovelty.reranking.ItemNoveltyReranker;
import es.uam.eps.ir.ranksys.novelty.sales.ISDCItemNovelty;

/**
 * Inter-System Discovery Complement re-ranker.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
*/
public class ISDCItemNoveltyReranker<U, I> extends ItemNoveltyReranker<U, I> {

    /**
     * Constructor.
     *
     * @param lambda trade-off between relevance and novelty
     * @param novelty item novelty model
     * @param norm normalize the relevance and novelty scores
     */
    public ISDCItemNoveltyReranker(double lambda, ISDCItemNovelty<U, I> novelty, boolean norm) {
        super(lambda, novelty, norm);
    }
}
