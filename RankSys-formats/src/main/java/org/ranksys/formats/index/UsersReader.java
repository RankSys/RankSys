/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.index;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.ranksys.formats.parsing.Parser;

/**
 * Reader for lists of users.
 * 
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class UsersReader {

    /**
     * Reads a list of users from a file.
     *
     * @param <U> user type
     * @param path path to file
     * @param up user parser
     * @return stream of users
     * @throws IOException when I/O problems
     */
    public static <U> Stream<U> read(String path, Parser<U> up) throws IOException {
        return read(new FileInputStream(path), up);
    }

    /**
     * Reads a list of users from an input stream.
     *
     * @param <U> user type
     * @param in input stream to read from
     * @param up user parser
     * @return stream of users
     * @throws IOException when I/O problems
     */
    public static <U> Stream<U> read(InputStream in, Parser<U> up) throws IOException {
        return Utils.readElemens(in, up);
    }

}
