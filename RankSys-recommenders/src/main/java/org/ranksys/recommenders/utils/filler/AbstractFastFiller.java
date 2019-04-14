/* 
 * Copyright (C) 2019 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.utils.filler;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.Recommendation;
import org.ranksys.core.fast.FastRecommendation;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Abstract class for implementing classes which fill a recommendation with some new data.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public abstract class AbstractFastFiller<U,I> implements FastFiller<U,I>
{
    /**
     * Accumulated number of filled items in recommendations
     */
    private int filled;
    /**
     * Total number of recommended items.
     */
    private int total;
    /**
     * Fast preference data.
     */
    protected FastPreferenceData<U,I> prefData;
    
    /**
     * Constructor.
     * @param prefData preference data.
     */
    public AbstractFastFiller(FastPreferenceData<U,I> prefData)
    {
        this.prefData = prefData;
        this.filled = 0;
        this.total = 0;
    }
    
    @Override
    public FastRecommendation fastFill(FastRecommendation rec, int cutoff, Function<U, IntPredicate> pred)
    {
        if(rec == null || cutoff < 0) return null;
        FastRecommendation aux;
        List<Tuple2id> original = rec.getIidxs();
        int originalSize = original.size();
        
        int uidx = rec.getUidx();
        
        if(originalSize == cutoff)
        {
            aux = rec;
            this.total += originalSize;
        }
        else if(originalSize > cutoff)
        {
            aux = new FastRecommendation(uidx, original.subList(0, cutoff));
            this.total += cutoff;
            this.filled += (cutoff - originalSize);
        }
        else // originalSize < cutoff
        {
            List<Tuple2id> tuples = new ArrayList<>();
            IntSet items = new IntOpenHashSet();
            original.forEach(iidx -> {
                tuples.add(iidx);
                items.add(iidx.v1);
            });
            
            double lastValue = originalSize > 0 ? original.get(originalSize-1).v2 : 1.0;
            
            AtomicInteger atomInt = new AtomicInteger(0);
            IntPredicate filter = pred.apply(this.prefData.uidx2user(uidx));
            IntStream stream = this.fillerList(uidx).filter(filter).filter(x -> !items.contains(x)).limit(cutoff-originalSize);
            if(stream != null && stream != IntStream.empty())
            {
                stream.forEach(elem -> tuples.add(new Tuple2id(elem, lastValue/(atomInt.incrementAndGet()))));
            }
            this.filled += atomInt.get();
            this.total += originalSize + atomInt.get();
            aux = new FastRecommendation(uidx, tuples);
        }
        return aux;
    }

    @Override
    public Recommendation<U, I> fill(Recommendation<U, I> rec, int cutoff, Function<U, Predicate<I>> pred)
    {
        if(rec == null || cutoff < 0) return null;
        Recommendation<U,I> aux;
        List<Tuple2od<I>> original = rec.getItems();
        int originalSize = original.size();
        
        U uidx = rec.getUser();
        
        if(originalSize == cutoff)
        {
            aux = rec;
            this.total += originalSize;
        }
        else if(originalSize > cutoff)
        {
            aux = new Recommendation<>(uidx, original.subList(0, cutoff));
            this.total += cutoff;
            this.filled += (cutoff - originalSize);
        }
        else // originalSize < cutoff
        {
            List<Tuple2od<I>> tuples = new ArrayList<>();
            Set<I> items = new HashSet<>();
            original.forEach(iidx -> {
                tuples.add(iidx);
                items.add(iidx.v1);
            });
            
            double lastValue = originalSize > 0 ? original.get(originalSize-1).v2 : 1.0;
            
            AtomicInteger atomInt = new AtomicInteger(0);
            Predicate<I> filter = pred.apply(uidx);
            Stream<I> stream = this.fillerList(uidx).filter(filter).filter(x -> !items.contains(x)).limit(cutoff-originalSize);
            if(stream != null && stream != Stream.empty())
            {
                stream.forEach(elem -> tuples.add(new Tuple2od<>(elem, lastValue/(atomInt.incrementAndGet()))));
            }
            this.filled += atomInt.get();
            this.total += originalSize + atomInt.get();
            aux = new Recommendation<>(uidx, tuples);
        }
        return aux;
    }

    @Override
    public int numFilled()
    {
        return this.filled;
    }

    @Override
    public int numTotal()
    {
        return this.total;
    }

    @Override
    public void reset()
    {
        this.filled = 0;
        this.total = 0;
        this.resetMethod();
    }

    /**
     * Resets the variables corresponding to the filler.
     */
    protected abstract void resetMethod();
}
