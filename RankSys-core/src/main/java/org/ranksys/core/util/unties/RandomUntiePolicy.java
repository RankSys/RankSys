/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ranksys.core.util.unties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.Recommendation;
import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.util.filler.FastFiller;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Provides a random order.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <U> Type of the users.
 * @param <I> Type of the items.
 */
public class RandomUntiePolicy<U,I> implements UntiePolicy<Integer>, FastFiller<U,I>
{
    /**
     * The preference data.
     */
    private final FastPreferenceData<U,I> prefData;
    
    /**
     * The given order for adding users.
     */
    private final List<Integer> order = new ArrayList<>();
    /**
     * A map containing the order.
     */
    private final Map<Integer, Integer> ordermap = new HashMap<>();
    private final Random rng;
    
    private int filled;
    private int total;
    
    
    
    public RandomUntiePolicy(FastPreferenceData<U,I> prefData, int seed)
    {
        this.prefData = prefData;
        IntStream.range(0, prefData.numItems()).forEach(i -> order.add(i));
        this.rng = new Random(seed);
        Collections.shuffle(order, rng);
        IntStream.range(0, prefData.numItems()).forEach(i -> ordermap.put(order.get(i), i));
        this.filled = 0;
    }
    
    public RandomUntiePolicy(FastPreferenceData<U,I> prefData)
    {
        this.prefData = prefData;
        IntStream.range(0, prefData.numItems()).forEach(i -> order.add(i));
        this.rng = new Random();
        Collections.shuffle(order, rng);
        IntStream.range(0, prefData.numItems()).forEach(i -> ordermap.put(order.get(i), i));
    }

    @Override
    public Comparator<Integer> comparator()
    {
        return (x,y) -> (ordermap.get(y) - ordermap.get(x));
    }

    @Override
    public void update()
    {
        if(this.prefData.numItems() != order.size())
        {
            IntStream.range(0, prefData.numItems()).forEach(i -> order.add(i));
            Collections.shuffle(order);
            IntStream.range(0, prefData.numItems()).forEach(i -> ordermap.put(order.get(i), i));
        }
    }

    @Override
    public IntStream fillerList(int uidx)
    {
        return order.stream().mapToInt(x->x);
    }

    @Override
    public Stream<I> fillerList(U u)
    {
        return order.stream().map(iidx -> this.prefData.iidx2item(iidx));
    }

    @Override
    public Recommendation<U, I> fastFill(Recommendation<U, I> rec, int cutoff, Function<U, IntPredicate> pred)
    {
        if(rec == null || cutoff < 0) return null;
        Recommendation<U,I> aux;
        List<Tuple2od<I>> tuples = new ArrayList<>();
        List<Tuple2od<I>> original = rec.getItems();
        
        int originalSize = original.size();
        U u = rec.getUser();
        if(originalSize == cutoff)
        {
            aux = rec;
            
            this.total += originalSize;
        }
        else if(originalSize > cutoff) // Trim the list.
        {
            aux = new Recommendation<>(u, original.subList(0, cutoff));
            this.total += cutoff;
            this.filled += cutoff - originalSize;
        }
        else // originalSize < cutoff
        {
            Set<Integer> items = original.stream().map(item -> 
            {
                tuples.add(item);
                return this.prefData.item2iidx(item.v1);
            }).collect(Collectors.toCollection(HashSet::new));
            
            double lastValue = originalSize > 0 ? original.get(originalSize-1).v2 : 1.0;
            int limit = cutoff - originalSize;
            
            IntPredicate filter = pred.apply(u);
            AtomicInteger atomInt = new AtomicInteger(0);
            this.order.stream().filter(iidx -> !items.contains(iidx) && filter.test(iidx)).limit(limit)
                    .map(iidx -> this.prefData.iidx2item(iidx))
                    .forEach(i -> tuples.add(new Tuple2od<>(i, lastValue/(atomInt.incrementAndGet()))));
            this.filled += atomInt.get();
            this.total += atomInt.get() + originalSize;
            aux = new Recommendation<>(u, tuples);
        }
        return aux;
    }

    @Override
    public Recommendation<U, I> fill(Recommendation<U, I> rec, int cutoff, Function<U, Predicate<I>> pred)
    {
        if(rec == null || cutoff < 0) return null;
        Recommendation<U,I> aux;
        List<Tuple2od<I>> tuples = new ArrayList<>();
        List<Tuple2od<I>> original = rec.getItems();
        
        int originalSize = original.size();
        U u = rec.getUser();
        if(originalSize == cutoff)
        {
            return rec;
        }
        else if(originalSize > cutoff) // Trim the list.
        {
            aux = new Recommendation<>(u, original.subList(0, cutoff));
        }
        else // originalSize < cutoff
        {
            Set<I> items = original.stream().map(item -> 
            {
                tuples.add(item);
                return item.v1;
            }).collect(Collectors.toCollection(HashSet::new));
            
            double lastValue = originalSize > 0 ? original.get(originalSize-1).v2 : 1.0;
            int limit = cutoff - originalSize;
            AtomicInteger atomInt = new AtomicInteger(0);
            Predicate<I> filter = pred.apply(u);
            this.order.stream().map(iidx -> this.prefData.iidx2item(iidx))
                      .filter(i -> !items.contains(i) && filter.test(i)).limit(limit)
                      .forEach(i -> tuples.add(new Tuple2od<>(i, lastValue/(atomInt.incrementAndGet()))));
            
            
            this.filled += atomInt.get();
            aux = new Recommendation<>(u, tuples);
        }
        return aux;
    }

    @Override
    public int numFilled()
    {
        return filled;
    }
    
    public int numTotal()
    {
        return total;
    }

    @Override
    public void reset()
    {
        this.filled = 0;
        this.total = 0;
    }
}
