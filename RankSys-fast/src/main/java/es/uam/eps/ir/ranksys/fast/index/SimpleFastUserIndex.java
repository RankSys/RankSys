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
public class SimpleFastUserIndex<U> implements FastUserIndex<U>, Serializable{

    private final IdxIndex<U> uMap;

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
     * Creates a user index from a file where the first column lists the users.
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

    public static <U> SimpleFastUserIndex<U> load(String path, Parser<U> uParser, boolean sort) throws IOException {
        return load(new FileInputStream(path), uParser, sort);
    }

    /**
     * Creates a user index from an input stream where the first column lists the users.
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

    public static <U> SimpleFastUserIndex<U> load(InputStream in, Parser<U> uParser, boolean sort) throws IOException {
        SimpleFastUserIndex<U> userIndex = new SimpleFastUserIndex<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Stream<U> users = reader.lines()
                    .map(line -> uParser.parse(split(line, '\t')[0]));
            (sort ? users.sorted() : users).forEach(u -> userIndex.add(u));
        }
        return userIndex;
    }
}
