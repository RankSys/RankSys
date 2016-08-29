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
 * Reader for lists of items.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class ItemsReader {

    /**
     * Reads a list of items from a file.
     *
     * @param <I> item type
     * @param path path to file
     * @param ip item parser
     * @return stream of items
     * @throws IOException when I/O problems
     */
    public static <I> Stream<I> read(String path, Parser<I> ip) throws IOException {
        return read(new FileInputStream(path), ip);
    }

    /**
     * Reads a list of items from an input stream.
     *
     * @param <I> item type
     * @param in input stream to read from
     * @param ip item parser
     * @return stream of items
     * @throws IOException when I/O problems
     */
    public static <I> Stream<I> read(InputStream in, Parser<I> ip) throws IOException {
        return Utils.readElemens(in, ip);
    }
}
