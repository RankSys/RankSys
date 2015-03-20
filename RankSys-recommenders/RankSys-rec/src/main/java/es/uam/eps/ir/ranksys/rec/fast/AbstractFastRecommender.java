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
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class AbstractFastRecommender<U, I> extends AbstractRecommender<U, I> implements FastRecommender<U, I> {

    protected final FastPreferenceData<U, I, ?> fastData;

    public AbstractFastRecommender(FastPreferenceData<U, I, ?> data) {
        super(data);

        this.fastData = data;
    }

    @Override
    public int numUsers() {
        return fastData.numUsers();
    }

    @Override
    public int user2uidx(U u) {
        return fastData.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return fastData.uidx2user(uidx);
    }

    @Override
    public int numItems() {
        return fastData.numItems();
    }

    @Override
    public int item2iidx(I i) {
        return fastData.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return fastData.iidx2item(iidx);
    }

    @Override
    public FastRecommendation<U, I> getRecommendation(int uidx, int maxLength) {
        return getRecommendation(uidx, maxLength, iidx -> true);
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter) {
        FastRecommendation<U, I> rec = getRecommendation(user2uidx(u), maxLength, iidx -> filter.test(iidx2item(iidx)));

        return new Recommendation<>(uidx2user(rec.getUidx()), rec.getIidxs().stream().map(iv -> new IdDouble<>(iidx2item(iv.idx), iv.v)).collect(Collectors.toList()));
    }

    @Override
    public abstract FastRecommendation<U, I> getRecommendation(int uidx, int maxLength, IntPredicate filter);

    @Override
    public FastRecommendation<U, I> getRecommendation(int uidx, IntStream candidates) {
        IntSet set = new IntOpenHashSet();
        candidates.forEach(iidx -> set.add(iidx));

        return getRecommendation(uidx, 0, item -> set.contains(item));
    }
}
