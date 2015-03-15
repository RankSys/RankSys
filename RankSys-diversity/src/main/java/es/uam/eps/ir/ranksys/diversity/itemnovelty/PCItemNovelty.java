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
package es.uam.eps.ir.ranksys.diversity.itemnovelty;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class PCItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final UserPCItemNoveltyModel nov;

    public PCItemNovelty(RecommenderData<U, I, ?> recommenderData) {
        super();
        this.nov = new UserPCItemNoveltyModel(recommenderData);
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U t) {
        return nov;
    }

    @Override
    public UserItemNoveltyModel<U, I> getModel(U u) {
        return nov;
    }

    private class UserPCItemNoveltyModel implements UserItemNoveltyModel<U, I> {

        private final TObjectDoubleMap<I> itemNovelty;

        public UserPCItemNoveltyModel(RecommenderData<U, I, ?> recommenderData) {
            itemNovelty = new TObjectDoubleHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 1.0);
            int numUsers = recommenderData.numUsersWithPreferences();
            recommenderData.getItemsWithPreferences().forEach(i -> {
                itemNovelty.put(i, 1 - recommenderData.numUsers(i) / (double) numUsers);
            });
        }

        @Override
        public double novelty(I i) {
            return itemNovelty.get(i);
        }

    }
}
