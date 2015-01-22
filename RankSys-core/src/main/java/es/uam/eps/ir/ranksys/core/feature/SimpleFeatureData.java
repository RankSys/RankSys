/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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

import es.uam.eps.ir.ranksys.core.IdVar;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class SimpleFeatureData<I, F, V> implements FeatureData<I, F, V> {

    private final Map<I, List<IdVar<F, V>>> itemMap;
    private final Map<F, List<IdVar<I, V>>> featMap;

    protected SimpleFeatureData(Map<I, List<IdVar<F, V>>> itemMap, Map<F, List<IdVar<I, V>>> featMap) {
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
    public Stream<IdVar<I, V>> getFeatureItems(F f) {
        return featMap.getOrDefault(f, Collections.EMPTY_LIST).stream();
    }

    @Override
    public Stream<IdVar<F, V>> getItemFeatures(I i) {
        return itemMap.getOrDefault(i, Collections.EMPTY_LIST).stream();
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
        return itemMap.getOrDefault(i, Collections.EMPTY_LIST).size();
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
        return featMap.getOrDefault(f, Collections.EMPTY_LIST).size();
    }

    public static <I, F, V> SimpleFeatureData<I, F, V> load(String path, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser) throws IOException {
        return load(new FileInputStream(path), iParser, fParser, vParser);
    }

    public static <I, F, V> SimpleFeatureData<I, F, V> load(InputStream in, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser) throws IOException {
        Map<I, List<IdVar<F, V>>> itemMap = new HashMap<>();
        Map<F, List<IdVar<I, V>>> featMap = new HashMap<>();

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

                List<IdVar<F, V>> iList = itemMap.get(item);
                if (iList == null) {
                    iList = new ArrayList<>();
                    itemMap.put(item, iList);
                }
                iList.add(new IdVar<>(feat, value));

                List<IdVar<I, V>> fList = featMap.get(feat);
                if (fList == null) {
                    fList = new ArrayList<>();
                    featMap.put(feat, fList);
                }
                fList.add(new IdVar<>(item, value));
            });
        }

        return new SimpleFeatureData<>(itemMap, featMap);
    }

}
