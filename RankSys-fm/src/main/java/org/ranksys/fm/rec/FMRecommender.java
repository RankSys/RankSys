/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.rec;

import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import static java.lang.Float.NaN;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.ranksys.javafm.FM;
import org.ranksys.javafm.instance.FMInstance;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class FMRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final FM<FMInstance> fm;
    private final IntFunction<Function<IdxPref, FMInstance>> instanceProvider;

    public FMRecommender(FM<FMInstance> fm, FastUserIndex<U> users, FastItemIndex<I> items, IntFunction<Function<IdxPref, FMInstance>> instanceProvider) {
        super(users, items);
        this.fm = fm;
        this.instanceProvider = instanceProvider;
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = numItems();
        }
        IntDoubleTopN topN = new IntDoubleTopN(maxLength);

        Function<IdxPref, FMInstance> uip = instanceProvider.apply(uidx);
        getAllIidx().filter(filter).forEach(iidx -> {
            FMInstance x = uip.apply(new IdxPref(iidx, NaN));
            if (x != null) {
                topN.add(iidx, fm.prediction(x));
            }
        });

        topN.sort();

        List<IdxDouble> items = topN.reverseStream()
                .map(e -> new IdxDouble(e))
                .collect(Collectors.toList());

        return new FastRecommendation(uidx, items);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        Function<IdxPref, FMInstance> uip = instanceProvider.apply(uidx);

        List<IdxDouble> items = candidates
                .mapToObj(iidx -> {
                    FMInstance x = uip.apply(new IdxPref(iidx, NaN));
                    if (x != null) {
                        return new IdxDouble(iidx, fm.prediction(x));
                    } else {
                        return new IdxDouble(iidx, NaN);
                    }
                })
                .sorted(comparingDouble((IdxDouble r) -> r.v).reversed())
                .collect(toList());

        return new FastRecommendation(uidx, items);
    }

}
