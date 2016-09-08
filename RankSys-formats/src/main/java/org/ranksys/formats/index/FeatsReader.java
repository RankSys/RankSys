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
 * Reader for list of feats (feature values).
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class FeatsReader {

    /**
     * Reads a list of feature values from a file.
     *
     * @param <F> feat type
     * @param path path to file
     * @param fp feat parser
     * @return stream of feats
     * @throws IOException when I/O problems
     */
    public static <F> Stream<F> read(String path, Parser<F> fp) throws IOException {
        return read(new FileInputStream(path), fp);
    }

    /**
     * Reads a list of feature values from an input stream.
     *
     * @param <F> feat type
     * @param in input stream to read from
     * @param fp feat parser
     * @return stream of feats
     * @throws IOException when I/O problems
     */
    public static <F> Stream<F> read(InputStream in, Parser<F> fp) throws IOException {
        return Utils.readElemens(in, fp);
    }
}
