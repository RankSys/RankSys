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
 * User similarity. It wraps a generic fast similarity and a fast user index.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public abstract class UserSimilarity<U> implements Similarity, FastUserIndex<U> {

    /**
     * Fast user index.
     */
    protected final FastUserIndex<U> uIndex;

    /**
     * Generic fast similarity.
     */
    protected final Similarity sim;

    /**
     * Constructor.
     *
     * @param uIndex fast user index
     * @param sim generic fast similarity
     */
    protected UserSimilarity(FastUserIndex<U> uIndex, Similarity sim) {
        this.uIndex = uIndex;
        this.sim = sim;
    }

    @Override
    public int numUsers() {
        return uIndex.numUsers();
    }

    @Override
    public int user2uidx(U u) {
        return uIndex.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uIndex.uidx2user(uidx);
    }

    /**
     * Returns a function returning similarities with the user
     *
     * @param u1 user
     * @return a function returning similarities with the user
     */
    public ToDoubleFunction<U> similarity(U u1) {
        return u2 -> sim.similarity(user2uidx(u1)).applyAsDouble(user2uidx(u2));
    }
    
    /**
     * Returns the similarity between a pair of users.
     *
     * @param u1 first user
     * @param u2 second user
     * @return similarity value between the users
     */
    public double similarity(U u1, U u2) {
        return sim.similarity(user2uidx(u1), user2uidx(u2));
    }

    /**
     * Returns all the users that are similar to the user.
     *
     * @param u user
     * @return a stream of user-similarity pairs
     */
    public Stream<IdDouble<U>> similarUsers(U u) {
        return similarUsers(user2uidx(u))
                .map(us -> new IdDouble<U>(uidx2user(us.idx), us.v));
    }

    /**
     * Returns all the users that are similar to the user - fast version.
     *
     * @param uidx user
     * @return a stream of user-similarity pairs
     */
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
