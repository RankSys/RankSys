/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.feature;

import es.uam.eps.ir.ranksys.core.IdValuePair;
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
 * @author saul
 */
public class SimpleFeatureData<I, F, V> implements FeatureData<I, F, V> {

    private final Map<I, List<IdValuePair<F, V>>> itemMap;
    private final Map<F, List<IdValuePair<I, V>>> featMap;

    protected SimpleFeatureData(Map<I, List<IdValuePair<F, V>>> itemMap, Map<F, List<IdValuePair<I, V>>> featMap) {
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
    public Stream<IdValuePair<I, V>> getFeatureItems(F f) {
        return featMap.getOrDefault(f, Collections.EMPTY_LIST).stream();
    }

    @Override
    public Stream<IdValuePair<F, V>> getItemFeatures(I i) {
        return itemMap.getOrDefault(i, Collections.EMPTY_LIST).stream();
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
        Map<I, List<IdValuePair<F, V>>> itemMap = new HashMap<>();
        Map<F, List<IdValuePair<I, V>>> featMap = new HashMap<>();

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

                List<IdValuePair<F, V>> iList = itemMap.get(item);
                if (iList == null) {
                    iList = new ArrayList<>();
                    itemMap.put(item, iList);
                }
                iList.add(new IdValuePair<>(feat, value));

                List<IdValuePair<I, V>> fList = featMap.get(feat);
                if (fList == null) {
                    fList = new ArrayList<>();
                    featMap.put(feat, fList);
                }
                fList.add(new IdValuePair<>(item, value));
            });
        }

        return new SimpleFeatureData<>(itemMap, featMap);
    }

}
