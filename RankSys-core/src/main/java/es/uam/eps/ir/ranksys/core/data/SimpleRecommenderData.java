/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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
package es.uam.eps.ir.ranksys.core.data;

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
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SimpleRecommenderData<U, I, V> implements RecommenderData<U, I, V> {

    private final Map<U, List<IdValuePair<I, V>>> userMap;
    private final Map<I, List<IdValuePair<U, V>>> itemMap;
    private final int numPreferences;

    protected SimpleRecommenderData(Map<U, List<IdValuePair<I, V>>> userMap, Map<I, List<IdValuePair<U, V>>> itemMap, int numPreferences) {
        this.userMap = userMap;
        this.itemMap = itemMap;
        this.numPreferences = numPreferences;
    }

    @Override
    public int numUsers() {
        return userMap.size();
    }

    @Override
    public int numUsers(I i) {
        return itemMap.getOrDefault(i, Collections.EMPTY_LIST).size();
    }

    @Override
    public int numItems() {
        return itemMap.size();
    }

    @Override
    public int numItems(U u) {
        return userMap.getOrDefault(u, Collections.EMPTY_LIST).size();
    }

    @Override
    public int numPreferences() {
        return numPreferences;
    }

    @Override
    public Stream<U> getAllUsers() {
        return userMap.keySet().stream();
    }

    @Override
    public Stream<I> getAllItems() {
        return itemMap.keySet().stream();
    }

    @Override
    public Stream<IdValuePair<I, V>> getUserPreferences(U u) {
        return userMap.getOrDefault(u, Collections.EMPTY_LIST).stream();
    }

    @Override
    public Stream<IdValuePair<U, V>> getItemPreferences(I i) {
        return itemMap.getOrDefault(i, Collections.EMPTY_LIST).stream();
    }

    public static <U, I, V> SimpleRecommenderData<U, I, V> load(String path, Parser<U> uParser, Parser<I> iParser, Parser<V> vParser) throws IOException {
        return load(new FileInputStream(path), uParser, iParser, vParser);
    }

    public static <U, I, V> SimpleRecommenderData<U, I, V> load(InputStream in, Parser<U> uParser, Parser<I> iParser, Parser<V> vParser) throws IOException {
        Map<U, List<IdValuePair<I, V>>> userMap = new HashMap<>();
        Map<I, List<IdValuePair<U, V>>> itemMap = new HashMap<>();
        int[] numPreferences = new int[]{0};

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(l -> {
                String[] tokens = l.split("\t", 3);
                U user = uParser.parse(tokens[0]);
                I item = iParser.parse(tokens[1]);
                V value = vParser.parse(tokens[2]);

                numPreferences[0]++;

                List<IdValuePair<I, V>> uList = userMap.get(user);
                if (uList == null) {
                    uList = new ArrayList<>();
                    userMap.put(user, uList);
                }
                uList.add(new IdValuePair<>(item, value));

                List<IdValuePair<U, V>> iList = itemMap.get(item);
                if (iList == null) {
                    iList = new ArrayList<>();
                    itemMap.put(item, iList);
                }
                iList.add(new IdValuePair<>(user, value));
            });
        }

        return new SimpleRecommenderData<>(userMap, itemMap, numPreferences[0]);
    }
    
}
