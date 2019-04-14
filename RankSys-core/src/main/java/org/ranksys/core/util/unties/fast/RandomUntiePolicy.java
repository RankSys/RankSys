/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ranksys.core.util.unties.fast;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.util.unties.fast.AbstractFastUntiePolicy;

/**
 * Provides a random order.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <T> the type of the element we are trying to compare.
 */
public class RandomUntiePolicy<T> extends AbstractFastUntiePolicy<T>
{
    /**
     * The given order.
     */
    private final IntList order;
    /**
     * A map containing the order.
     */
    private final Map<Integer, Integer> ordermap = new HashMap<>();
    /**
     * Random number generator.
     */
    private final Random rng;
    
    /**
     * Constructor.
     * @param index the index.
     * @param seed a random seed.
     */
    public RandomUntiePolicy(FastItemIndex<T> index, long seed)
    {
        super(index);
        this.rng = new Random(seed);
        this.order = new IntArrayList();
        index.getAllIidx().forEach(iidx -> order.add(iidx));
        Collections.shuffle(this.order, rng);
        IntStream.range(0, index.numItems()).forEach(i -> ordermap.put(order.get(i),i));
    }
    
    /**
     * Constructor.
     * @param index the index. 
     */
    public RandomUntiePolicy(FastItemIndex<T> index)
    {
        this(index, System.currentTimeMillis());
    }

    @Override
    public Comparator<T> comparator()
    {
        return (x,y) -> (ordermap.get(index.item2iidx(x)) - ordermap.get(index.item2iidx(y)));
    }

    @Override
    public void update()
    {       
        order.clear();
        this.index.getAllIidx().forEach(iidx -> order.add(iidx));
        Collections.shuffle(order,rng);
        ordermap.clear();
        IntStream.range(0, index.numItems()).forEach(i -> ordermap.put(order.get(i), i));
    }

    @Override
    public Comparator<Integer> fastComparator()
    {
        return (x,y) -> (ordermap.get(y) - ordermap.get(x));
    }
}
