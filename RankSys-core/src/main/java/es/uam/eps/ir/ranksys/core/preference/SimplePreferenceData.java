/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.preference;

import es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Simple map-based preference data
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class SimplePreferenceData<U, I> implements PreferenceData<U, I>, Serializable {

    private final Map<U, List<IdPref<I>>> userMap;
    private final Map<I, List<IdPref<U>>> itemMap;
    private final int numPreferences;

    /**
     * Constructor.
     *
     * @param userMap user to preferences map
     * @param itemMap item to preferences map
     * @param numPreferences total number of preferences
     */
    protected SimplePreferenceData(Map<U, List<IdPref<I>>> userMap, Map<I, List<IdPref<U>>> itemMap, int numPreferences) {
        this.userMap = userMap;
        this.itemMap = itemMap;
        this.numPreferences = numPreferences;
    }

    @Override
    public boolean containsUser(U u) {
        return userMap.containsKey(u);
    }

    @Override
    public int numUsers() {
        return userMap.size();
    }

    @Override
    public int numUsers(I i) {
        return itemMap.getOrDefault(i, new ArrayList<>(0)).size();
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
    public int numItems(U u) {
        return userMap.getOrDefault(u, new ArrayList<>()).size();
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
    public Stream<IdPref<I>> getUserPreferences(U u) {
        return userMap.getOrDefault(u, new ArrayList<>()).stream();
    }

    @Override
    public Stream<IdPref<U>> getItemPreferences(I i) {
        return itemMap.getOrDefault(i, new ArrayList<>()).stream();
    }

    @Override
    public int numUsersWithPreferences() {
        return userMap.size();
    }

    @Override
    public int numItemsWithPreferences() {
        return itemMap.size();
    }

    @Override
    public Stream<U> getUsersWithPreferences() {
        return userMap.keySet().stream();
    }

    @Override
    public Stream<I> getItemsWithPreferences() {
        return itemMap.keySet().stream();
    }

    /**
     * Load preferences from a file.
     * 
     * Each line is a different preference, with tab-separated fields indicating
     * user, item, weight and other information.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param path path of the input file
     * @param uParser user type parser
     * @param iParser item type parser
     * @param dp double parse
     * @return a simple map-based PreferenceData with the information read
     * @throws IOException when path does not exists of IO error
     */
    public static <U, I> SimplePreferenceData<U, I> load(String path, Parser<U> uParser, Parser<I> iParser, DoubleParser dp) throws IOException {
        return load(new FileInputStream(path), uParser, iParser, dp);
    }

    /**
     * Load preferences from an input stream.
     * 
     * Each line is a different preference, with tab-separated fields indicating
     * user, item, weight and other information.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param in input stream to read from
     * @param uParser user type parser
     * @param iParser item type parser
     * @param dp double parse
     * @return a simple map-based PreferenceData with the information read
     * @throws IOException when path does not exists of IO error
     */
    public static <U, I> SimplePreferenceData<U, I> load(InputStream in, Parser<U> uParser, Parser<I> iParser, DoubleParser dp) throws IOException {
        Map<U, List<IdPref<I>>> userMap = new HashMap<>();
        Map<I, List<IdPref<U>>> itemMap = new HashMap<>();
        AtomicInteger numPreferences = new AtomicInteger(0);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(l -> {
                String[] tokens = l.split("\t", 4);
                U user = uParser.parse(tokens[0]);
                I item = iParser.parse(tokens[1]);
                double value;
                if (tokens.length >= 3) {
                    value = dp.parse(tokens[2]);
                } else {
                    value = dp.parse(null);
                }

                numPreferences.incrementAndGet();

                List<IdPref<I>> uList = userMap.get(user);
                if (uList == null) {
                    uList = new ArrayList<>();
                    userMap.put(user, uList);
                }
                uList.add(new IdPref<>(item, value));

                List<IdPref<U>> iList = itemMap.get(item);
                if (iList == null) {
                    iList = new ArrayList<>();
                    itemMap.put(item, iList);
                }
                iList.add(new IdPref<>(user, value));
            });
        }

        return new SimplePreferenceData<>(userMap, itemMap, numPreferences.intValue());
    }

}
