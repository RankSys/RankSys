/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.fast;

import es.uam.eps.ir.ranksys.rec.AbstractRecommender;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Abstract (fast) recommender. It implements the free and candidate-based recommendation methods as variants of the filter recommendation.
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
    public Recommendation<U, I> getRecommendation(U u, int maxLength) {
        FastRecommendation rec = getRecommendation(user2uidx(u), maxLength);

        return new Recommendation<>(uidx2user(rec.getUidx()), rec.getIidxs().stream()
                .map(this::iidx2item)
                .collect(toList()));
    }

    @Override
    public FastRecommendation getRecommendation(int uidx) {
        return getRecommendation(uidx, Integer.MAX_VALUE);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength) {
        return getRecommendation(uidx, maxLength, iidx -> true);
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter) {
        FastRecommendation rec = getRecommendation(user2uidx(u), maxLength, iidx -> filter.test(iidx2item(iidx)));

        return new Recommendation<>(uidx2user(rec.getUidx()), rec.getIidxs().stream()
                .map(this::iidx2item)
                .collect(toList()));
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntPredicate filter) {
        return getRecommendation(uidx, Integer.MAX_VALUE, filter);
    }

    @Override
    public abstract FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter);

    @Override
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates) {
        FastRecommendation rec = getRecommendation(user2uidx(u), candidates.mapToInt(this::item2iidx));

        return new Recommendation<>(uidx2user(rec.getUidx()), rec.getIidxs().stream()
                .map(this::iidx2item)
                .collect(toList()));
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        IntSet set = new IntOpenHashSet();
        candidates.forEach(iidx -> set.add(iidx));

        return getRecommendation(uidx, item -> set.contains(item));
    }
}
