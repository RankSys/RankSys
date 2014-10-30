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
package es.uam.eps.ir.ranksys.core.data;

import es.uam.eps.ir.ranksys.core.IdValuePair;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class ConcatRecommenderData<U, I, V> implements RecommenderData<U, I, V> {

    private final RecommenderData<U, I, V> d1;
    private final RecommenderData<U, I, V> d2;

    public ConcatRecommenderData(RecommenderData<U, I, V> d1, RecommenderData<U, I, V> d2) {
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public int numUsers() {
        return (int) getAllUsers().count();
    }

    @Override
    public int numUsers(I i) {
        return d1.numUsers(i) + d2.numUsers(i);
    }

    @Override
    public int numItems() {
        return (int) getAllItems().count();
    }

    @Override
    public int numItems(U u) {
        return d1.numItems(u) + d2.numItems(u);
    }

    @Override
    public int numPreferences() {
        return d1.numPreferences() + d2.numPreferences();
    }

    @Override
    public Stream<U> getAllUsers() {
        return Stream.concat(d1.getAllUsers(), d2.getAllUsers()).distinct();
    }

    @Override
    public Stream<I> getAllItems() {
        return Stream.concat(d1.getAllItems(), d2.getAllItems()).distinct();
    }

    @Override
    public Stream<IdValuePair<I, V>> getUserPreferences(U u) {
        return Stream.concat(d1.getUserPreferences(u), d2.getUserPreferences(u));
    }

    @Override
    public Stream<IdValuePair<U, V>> getItemPreferences(I i) {
        return Stream.concat(d1.getItemPreferences(i), d2.getItemPreferences(i));
    }
}
