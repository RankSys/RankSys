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
package es.uam.eps.ir.ranksys.diversity.itemnovelty;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import es.uam.eps.ir.ranksys.diversity.pairwise.ItemDistanceModel;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class PDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final RecommenderData<U, I, ?> recommenderData;
    private final ItemDistanceModel<I> dist;

    public PDItemNovelty(boolean caching, RecommenderData<U, I, ?> recommenderData, ItemDistanceModel<I> dist) {
        super(caching, recommenderData.getAllUsers());
        this.recommenderData = recommenderData;
        this.dist = dist;
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U u) {
        return new UserPDItemNovelty(u);
    }

    private class UserPDItemNovelty implements UserItemNoveltyModel<U, I> {

        private final U u;

        public UserPDItemNovelty(U u) {
            this.u = u;
        }

        @Override
        public double novelty(I i) {
            return recommenderData.getUserPreferences(u)
                    .map(jv -> jv.id)
                    .mapToDouble(j -> dist.dist(i, j))
                    .filter(v -> !Double.isNaN(v))
                    .average().orElse(0.0);
        }

    }
}
