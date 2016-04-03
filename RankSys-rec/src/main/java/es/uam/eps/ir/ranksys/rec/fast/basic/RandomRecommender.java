/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.fast.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import static java.lang.Double.NaN;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2od;
import static java.util.Collections.shuffle;
import org.ranksys.core.util.tuples.Tuple2id;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Random recommender. It provides non-personalized recommendations without by extracting a sequence of a shuffled list of the items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class RandomRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final Random random;
    private final List<Tuple2id> randomList;

    /**
     * Constructor.
     *
     * @param uIndex fast user index
     * @param iIndex fast item index
     */
    public RandomRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);
        random = new Random();
        randomList = iIndex.getAllIidx()
                .mapToObj(iidx -> tuple(iidx, Double.NaN))
                .collect(toList());

        shuffle(randomList);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {

        int N = min(maxLength, randomList.size());
        List<Tuple2id> recommended = new ArrayList<>();
        int s = random.nextInt(randomList.size());
        int j = s;
        for (int i = 0; i < N; i++) {
            Tuple2id iv = randomList.get(j);
            while (!filter.test(iv.v1)) {
                j = (j + 1) % randomList.size();
                iv = randomList.get(j);
            }
            recommended.add(iv);
            j = (j + 1) % randomList.size();
            if (s == j) {
                break;
            }
        }

        return new FastRecommendation(uidx, recommended);
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates) {
        List<Tuple2od<I>> items = candidates.map(i -> tuple(i, NaN)).collect(toList());
        Collections.shuffle(items, random);

        return new Recommendation<>(u, items);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        List<Tuple2id> items = candidates.mapToObj(iidx -> tuple(iidx, NaN)).collect(toList());
        Collections.shuffle(items, random);

        return new FastRecommendation(uidx, items);
    }

}
