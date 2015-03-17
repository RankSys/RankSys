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
package es.uam.eps.ir.ranksys.nn.user.sim;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.nn.sim.Similarity;
import java.util.function.IntToDoubleFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U>
 */
public abstract class UserSimilarity<U> implements Similarity, FastUserIndex<U> {

    protected final FastUserIndex<U> indexedUser;
    protected final Similarity sim;

    protected UserSimilarity(FastUserIndex<U> indexedUser, Similarity sim) {
        this.indexedUser = indexedUser;
        this.sim = sim;
    }

    @Override
    public int numUsers() {
        return indexedUser.numUsers();
    }

    @Override
    public int user2uidx(U u) {
        return indexedUser.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return indexedUser.uidx2user(uidx);
    }

    public ToDoubleFunction<U> similarity(U u1) {
        return u2 -> sim.similarity(user2uidx(u1)).applyAsDouble(user2uidx(u2));
    }
    
    public double similarity(U u1, U u2) {
        return sim.similarity(user2uidx(u1), user2uidx(u2));
    }

    public Stream<IdDouble<U>> similarUsers(U u) {
        return similarUsers(user2uidx(u))
                .map(us -> new IdDouble<U>(uidx2user(us.idx), us.v));
    }

    public Stream<IdxDouble> similarUsers(int uidx) {
        return sim.similarElems(uidx);
    }

    @Override
    public IntToDoubleFunction similarity(int idx1) {
        return sim.similarity(idx1);
    }

    @Override
    public double similarity(int idx1, int idx2) {
        return sim.similarity(idx1, idx2);
    }

    @Override
    public Stream<IdxDouble> similarElems(int idx) {
        return sim.similarElems(idx);
    }

}
