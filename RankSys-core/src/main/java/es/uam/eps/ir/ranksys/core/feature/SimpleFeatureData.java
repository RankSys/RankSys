/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    private final Map<I, List<IdObject<F, V>>> itemMap;
    private final Map<F, List<IdObject<I, V>>> featMap;

    /**
     * Constructor
     *
     * @param itemMap item to features map
     * @param featMap feature to items map
     */
    protected SimpleFeatureData(Map<I, List<IdObject<F, V>>> itemMap, Map<F, List<IdObject<I, V>>> featMap) {
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
    public Stream<IdObject<I, V>> getFeatureItems(F f) {
        return featMap.getOrDefault(f, new ArrayList<>()).stream();
    }

    @Override
    public Stream<IdObject<F, V>> getItemFeatures(I i) {
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

    /**
     * Load feature data from a file.
     * 
     * Each line is a different item-feature pair, with tab-separated fields indicating
     * item, feature and other information.
     *
     * @param <I> type of the items
     * @param <F> type of the features
     * @param <V> type of the information about item-feature pairs
     * @param path file path
     * @param iParser item type parser
     * @param fParser feature type parser
     * @param vParser information type parser
     * @return a simple map-based FeatureData
     * @throws IOException when path does not exist or IO error
     */
    public static <I, F, V> SimpleFeatureData<I, F, V> load(String path, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser) throws IOException {
        return load(new FileInputStream(path), iParser, fParser, vParser);
    }

    /**
     * Load feature data from a input stream.
     * 
     * Each line is a different item-feature pair, with tab-separated fields indicating
     * item, feature and other information.
     *
     * @param <I> type of the items
     * @param <F> type of the features
     * @param <V> type of the information about item-feature pairs
     * @param in input stream
     * @param iParser item type parser
     * @param fParser feature type parser
     * @param vParser information type parser
     * @return a simple map-based FeatureData
     * @throws IOException when IO error
     */
    public static <I, F, V> SimpleFeatureData<I, F, V> load(InputStream in, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser) throws IOException {
        Map<I, List<IdObject<F, V>>> itemMap = new HashMap<>();
        Map<F, List<IdObject<I, V>>> featMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(l -> {
                String[] tokens = l.split("\t", 3);
                I item = iParser.parse(tokens[0]);
                F feat = fParser.parse(tokens[1]);
                V value;
                if (tokens.length == 2) {
                    value = vParser.parse(null);
                } else {
                    value = vParser.parse(tokens[2]);
                }

                List<IdObject<F, V>> iList = itemMap.get(item);
                if (iList == null) {
                    iList = new ArrayList<>();
                    itemMap.put(item, iList);
                }
                iList.add(new IdObject<>(feat, value));

                List<IdObject<I, V>> fList = featMap.get(feat);
                if (fList == null) {
                    fList = new ArrayList<>();
                    featMap.put(feat, fList);
                }
                fList.add(new IdObject<>(item, value));
            });
        }

        return new SimpleFeatureData<>(itemMap, featMap);
    }

}
