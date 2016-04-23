/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.utils.map;

import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.util.iterators.StreamIntIterator;
import org.ranksys.core.util.iterators.StreamObjectIterator;

/**
 * Specific integer to object map that does no hashing as 
 * it stores values in an array indexed by the key.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <V> type of values
 */
public class Int2ObjectDirectMap<V> extends AbstractInt2ObjectMap<V> {

    private final int minKey;
    private final int maxKey;
    private final Object[] value;
    private int size;

    /**
     * Constructor in which the smallest and largest keys allowed in the map
     * are specified.
     *
     * @param minKey smallest key
     * @param maxKey largest key
     */
    public Int2ObjectDirectMap(int minKey, int maxKey) {
        this.minKey = minKey;
        this.maxKey = maxKey;
        this.value = new Object[maxKey - minKey + 1];
        this.size = 0;
    }

    private final class EntrySet extends AbstractObjectSet<Int2ObjectMap.Entry<V>> implements FastEntrySet<V> {

        @Override
        public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
            return new StreamObjectIterator<>(keyStream()
                    .mapToObj(k -> new BasicEntry<V>(k, get(k))));
        }

        @Override
        public ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator() {
            return iterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<Integer, V> e = (Map.Entry<Integer, V>) o;
            return get(e.getKey()) != null && get(e.getKey()).equals(e.getValue());
        }
    }

    @Override
    public FastEntrySet<V> int2ObjectEntrySet() {
        return new EntrySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(final int k) {
        return (V) value[k - minKey];
    }

    /**
     * See {@link super#getOrDefault()}.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key
     */
    public V getOrDefault(int key, V defaultValue) {
        V v;
        return (v = get(key)) != null ? v : defaultValue;
    }

    /**
     * See {@link super#computeIfAbsent()}.
     *
     * @param key key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with
     *         the specified key, or null if the computed value is null
     */
    public V computeIfAbsent(int key, IntFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(value, null);
        size = 0;
    }

    @Override
    public boolean containsKey(final int k) {
        return get(k) != null;
    }

    @Override
    public boolean containsValue(Object v) {
        Objects.requireNonNull(v);
        return Stream.of(value).anyMatch(Predicate.isEqual(v));
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V put(int k, V v) {
        Objects.requireNonNull(v);
        V oldValue = get(k);
        value[k - minKey] = v;
        size++;

        return oldValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(final int k) {
        V oldValue = get(k);
        value[k - minKey] = null;
        size--;

        return oldValue;
    }

    /**
     *
     * @return
     */
    public IntStream keyStream() {
        return IntStream.rangeClosed(minKey, maxKey)
                .filter(k -> get(k) != null);
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Stream<V> valueStream() {
        return Stream.of(value)
                .filter(v -> v != null)
                .map(v -> (V) v);
    }

    @Override
    public IntSet keySet() {
        return new AbstractIntSet() {
            @Override
            public IntIterator iterator() {
                return new StreamIntIterator(keyStream());
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
                return new StreamObjectIterator<>(valueStream());
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

}
