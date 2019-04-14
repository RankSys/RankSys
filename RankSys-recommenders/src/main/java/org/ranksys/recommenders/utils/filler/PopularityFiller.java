/* 
 * Copyright (C) 2019 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.utils.filler;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import static java.util.Comparator.comparingDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Filler that completes a recommendation ranking using the popularity of the rankings.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <U> type of the users.
 * @param <I> type of the items.
 */
public class PopularityFiller<U,I> extends AbstractFastFiller<U,I>
{
    /**
     * Popularity ordered list.
     */
    private final IntList order;

    /**
     * Constructor.
     * @param prefData preference data.
     */
    public PopularityFiller(FastPreferenceData<U, I> prefData)
    {
        super(prefData);
        this.order = this.prefData.getAllIidx().mapToObj(iidx -> new Tuple2id(iidx, this.prefData.numUsers(iidx) + 0.0)).sorted(comparingDouble(Tuple2id::v2).reversed())
                .map(x -> x.v1).collect(Collectors.toCollection(IntArrayList::new));
    }
    
    @Override
    protected void resetMethod()
    {
        this.order.clear();
        this.prefData.getAllIidx().mapToObj(iidx -> new Tuple2id(iidx, this.prefData.numUsers(iidx) + 0.0)).sorted(comparingDouble(Tuple2id::v2).reversed())
                .forEach(x -> this.order.add(x.v1));
    }

    @Override
    public IntStream fillerList(int uidx)
    {
        return order.stream().mapToInt(x -> x);
    }

    @Override
    public Stream<I> fillerList(U u)
    {
        return order.stream().map(x -> this.prefData.iidx2item(x));
    }

}
