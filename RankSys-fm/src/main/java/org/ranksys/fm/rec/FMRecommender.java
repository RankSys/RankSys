/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.rec;

import org.ranksys.core.fast.FastRecommendation;
import org.ranksys.core.preference.fast.IdxPref;
import org.ranksys.core.util.topn.IntDoubleTopN;
import org.ranksys.rec.fast.AbstractFastRecommender;
import static java.lang.Float.NaN;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.ranksys.core.util.tuples.Tuple2id;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.fm.PreferenceFM;

/**
 * A recommender using a factorisation machine.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <U> user type
 * @param <I> item type
 */
public class FMRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final PreferenceFM fm;

    /**
     * Constructor.
     *
     * @param fm factorisation machine
     */
    public FMRecommender(PreferenceFM<U, I> fm) {
        super(fm, fm);
        this.fm = fm;
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = numItems();
        }
        IntDoubleTopN topN = new IntDoubleTopN(maxLength);

        getAllIidx().filter(filter).forEach(iidx -> {
            topN.add(iidx, fm.predict(uidx, new IdxPref(iidx, NaN)));
        });

        topN.sort();

        List<Tuple2id> items = topN.reverseStream()
                .collect(toList());

        return new FastRecommendation(uidx, items);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        List<Tuple2id> items = candidates
                .mapToObj(iidx -> {
                    return tuple(iidx, fm.predict(uidx, new IdxPref(iidx, NaN)));
                })
                .sorted(comparingDouble(Tuple2id::v2).reversed())
                .collect(toList());

        return new FastRecommendation(uidx, items);
    }

}
