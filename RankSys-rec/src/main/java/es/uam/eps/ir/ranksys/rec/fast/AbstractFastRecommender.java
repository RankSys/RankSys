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
package es.uam.eps.ir.ranksys.rec.fast;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.rec.AbstractRecommender;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Abstract (fast) recommender. It implements the free and candidate-based 
 * recommendation methods as variants of the filter recommendation.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class AbstractFastRecommender<U, I> extends AbstractRecommender<U, I> implements FastRecommender<U, I> {

    /**
     * Fast user index.
     */
    protected final FastUserIndex<U> uIndex;

    /**
     * Fast item index.
     */
    protected final FastItemIndex<I> iIndex;

    /**
     * Constructor.
     *
     * @param uIndex user index
     * @param iIndex item index
     */
    public AbstractFastRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super();

        this.uIndex = uIndex;
        this.iIndex = iIndex;
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

    @Override
    public int numItems() {
        return iIndex.numItems();
    }

    @Override
    public int item2iidx(I i) {
        return iIndex.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return iIndex.iidx2item(iidx);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength) {
        return getRecommendation(uidx, maxLength, iidx -> true);
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter) {
        FastRecommendation rec = getRecommendation(user2uidx(u), maxLength, iidx -> filter.test(iidx2item(iidx)));

        return new Recommendation<>(uidx2user(rec.getUidx()), rec.getIidxs().stream().map(iv -> new IdDouble<>(iidx2item(iv.idx), iv.v)).collect(Collectors.toList()));
    }

    @Override
    public abstract FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter);

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        IntSet set = new IntOpenHashSet();
        candidates.forEach(iidx -> set.add(iidx));

        return getRecommendation(uidx, 0, item -> set.contains(item));
    }
}
