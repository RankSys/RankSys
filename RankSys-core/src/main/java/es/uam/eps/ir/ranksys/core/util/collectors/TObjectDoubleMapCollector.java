/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.util.collectors;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @author saul
 */
public class TObjectDoubleMapCollector<T> implements Collector<IdDoublePair<T>, TObjectDoubleMap<T>, TObjectDoubleMap<T>> {

    @Override
    public Supplier<TObjectDoubleMap<T>> supplier() {
        return () -> new TObjectDoubleHashMap<>();
    }

    @Override
    public BiConsumer<TObjectDoubleMap<T>, IdDoublePair<T>> accumulator() {
        return (m, iv) -> m.put(iv.id, iv.v);
    }

    @Override
    public BinaryOperator<TObjectDoubleMap<T>> combiner() {
        return (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        };
    }

    @Override
    public Function<TObjectDoubleMap<T>, TObjectDoubleMap<T>> finisher() {
        return m -> m;
    }

    @Override
    public Set<Characteristics> characteristics() {
        Set<Characteristics> set = new HashSet<>();
        set.add(Characteristics.UNORDERED);
        set.add(Characteristics.IDENTITY_FINISH);
        return set;
    }

}
