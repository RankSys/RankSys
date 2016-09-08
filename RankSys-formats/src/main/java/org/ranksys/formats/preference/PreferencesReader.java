/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.preference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parser;

/**
 * Reader for files containing preference data.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public interface PreferencesReader {

    /**
     * Reads preferences from a file.
     *
     * @param <U> user type
     * @param <I> item tpye
     * @param in path to file
     * @param up user parser
     * @param ip item parser
     * @return stream of user-item-value triples
     * @throws IOException when I/O problems
     */
    public default <U, I> Stream<Tuple3<U, I, Double>> read(String in, Parser<U> up, Parser<I> ip) throws IOException {
        return read(new FileInputStream(in), up, ip);
    }

    /**
     * Reads preferences from an input stream.
     *
     * @param <U> user type
     * @param <I> item tpye
     * @param in input stream to read from
     * @param up user parser
     * @param ip item parser
     * @return stream of user-item-value triples
     * @throws IOException when I/O problems
     */
    public <U, I> Stream<Tuple3<U, I, Double>> read(InputStream in, Parser<U> up, Parser<I> ip) throws IOException;

}
