/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.index;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Simple implementation of FastUserIndex backed by a bi-map IdxIndex
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 */
public class SimpleFastUserIndex<U> implements FastUserIndex<U>, Serializable {

    private final IdxIndex<U> uMap;

    /**
     * Empty constructor: no users.
     */
    protected SimpleFastUserIndex() {
        this.uMap = new IdxIndex<>();
    }

    @Override
    public boolean containsUser(U u) {
        return uMap.containsId(u);
    }

    @Override
    public int numUsers() {
        return uMap.size();
    }

    @Override
    public Stream<U> getAllUsers() {
        return uMap.getIds();
    }

    @Override
    public int user2uidx(U u) {
        return uMap.get(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return uMap.get(uidx);
    }

    /**
     * Add a new user to the index. If the user already exists, nothing is done.
     *
     * @param u id of the user
     * @return index of the user
     */
    protected int add(U u) {
        return uMap.add(u);
    }

    /**
     * Creates a user index from a file where the first column lists the users. This method sorts the users ids and then assigns integer ids in that order.
     *
     * @param <U> type of the users
     * @param path path of the file
     * @param uParser user type parser
     * @return a fast user index
     * @throws IOException when file does not exist or when IO error
     */
    public static <U> SimpleFastUserIndex<U> load(String path, Parser<U> uParser) throws IOException {
        return load(path, uParser, true);
    }

    /**
     * Creates a user index from a file where the first column lists the users.
     *
     * @param <U> type of the users
     * @param path path of the file
     * @param uParser user type parser
     * @param sort if true, user ids in the file are sorted before assigning integer indices
     * @return a fast user index
     * @throws IOException when file does not exist or when IO error
     */
    public static <U> SimpleFastUserIndex<U> load(String path, Parser<U> uParser, boolean sort) throws IOException {
        return load(new FileInputStream(path), uParser, sort);
    }

    /**
     * Creates a user index from an input stream where the first column lists the users. This method sorts the users ids and then assigns integer ids in that order.
     *
     * @param <U> type of the users
     * @param in input stream
     * @param uParser user type parser
     * @return a fast user index
     * @throws IOException when IO error
     */
    public static <U> SimpleFastUserIndex<U> load(InputStream in, Parser<U> uParser) throws IOException {
        return load(in, uParser, true);
    }

    /**
     * Creates a user index from an input stream where the first column lists the users. This method sorts the users ids and then assigns integer ids in that order.
     *
     * @param <U> type of the users
     * @param in input stream
     * @param uParser user type parser
     * @param sort if true, user ids in the stream are sorted before assigning integer indices
     * @return a fast user index
     * @throws IOException when IO error
     */
    public static <U> SimpleFastUserIndex<U> load(InputStream in, Parser<U> uParser, boolean sort) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<U> users = reader.lines()
                    .map(line -> uParser.parse(split(line, '\t')[0]));
            return load(sort ? users.sorted() : users);
        }
    }

    /**
     * Creates a user index from a stream of user objects.
     *
     * @param <U> type of the users
     * @param users stream of user objects
     * @return a fast user index
     */
    public static <U> SimpleFastUserIndex<U> load(Stream<U> users) {
        SimpleFastUserIndex<U> userIndex = new SimpleFastUserIndex<>();
        users.forEach(userIndex::add);
        return userIndex;
    }
}
