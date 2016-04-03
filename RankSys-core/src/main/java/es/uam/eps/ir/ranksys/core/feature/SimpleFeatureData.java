/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.jooq.lambda.tuple.Tuple.tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Simple map-based feature data.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <I> type of the items
 * @param <F> type of the features
 * @param <V> type of the information about item-feature pairs
 */
public class SimpleFeatureData<I, F, V> implements FeatureData<I, F, V> {

    private final Map<I, List<Tuple2<F, V>>> itemMap;
    private final Map<F, List<Tuple2<I, V>>> featMap;

    /**
     * Constructor
     *
     * @param itemMap item to features map
     * @param featMap feature to items map
     */
    protected SimpleFeatureData(Map<I, List<Tuple2<F, V>>> itemMap, Map<F, List<Tuple2<I, V>>> featMap) {
        this.itemMap = itemMap;
        this.featMap = featMap;
    }

    @Override
    public Stream<F> getAllFeatures() {
        return featMap.keySet().stream();
    }

    @Override
    public Stream<I> getAllItems() {
        return itemMap.keySet().stream();
    }

    @Override
    public Stream<Tuple2<I, V>> getFeatureItems(F f) {
        return featMap.getOrDefault(f, new ArrayList<>()).stream();
    }

    @Override
    public Stream<Tuple2<F, V>> getItemFeatures(I i) {
        return itemMap.getOrDefault(i, new ArrayList<>()).stream();
    }

    @Override
    public boolean containsFeature(F f) {
        return featMap.containsKey(f);
    }

    @Override
    public int numFeatures() {
        return featMap.size();
    }

    @Override
    public int numFeatures(I i) {
        return itemMap.getOrDefault(i, new ArrayList<>()).size();
    }

    @Override
    public boolean containsItem(I i) {
        return itemMap.containsKey(i);
    }

    @Override
    public int numItems() {
        return itemMap.size();
    }

    @Override
    public int numItems(F f) {
        return featMap.getOrDefault(f, new ArrayList<>()).size();
    }

    @Override
    public int numItemsWithFeatures() {
        return itemMap.size();
    }

    @Override
    public int numFeaturesWithItems() {
        return featMap.size();
    }

    @Override
    public Stream<I> getItemsWithFeatures() {
        return itemMap.keySet().stream();
    }

    @Override
    public Stream<F> getFeaturesWithItems() {
        return featMap.keySet().stream();
    }

    public static <I, F, V> SimpleFeatureData<I, F, V> load(Stream<Tuple3<I, F, V>> tuples) {
        Map<I, List<Tuple2<F, V>>> itemMap = new HashMap<>();
        Map<F, List<Tuple2<I, V>>> featMap = new HashMap<>();

        tuples.forEach(t -> {
            List<Tuple2<F, V>> iList = itemMap.get(t.v1);
            if (iList == null) {
                iList = new ArrayList<>();
                itemMap.put(t.v1, iList);
            }
            iList.add(tuple(t.v2, t.v3));

            List<Tuple2<I, V>> fList = featMap.get(t.v2);
            if (fList == null) {
                fList = new ArrayList<>();
                featMap.put(t.v2, fList);
            }
            fList.add(tuple(t.v1, t.v3));
        });

        return new SimpleFeatureData<>(itemMap, featMap);
    }
}
