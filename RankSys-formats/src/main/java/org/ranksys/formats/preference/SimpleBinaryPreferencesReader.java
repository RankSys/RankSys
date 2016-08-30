/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.preference;

import static org.ranksys.core.util.FastStringSplitter.split;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parser;

/**
 * Reads a file of tab-separated user-item pairs, one per line.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleBinaryPreferencesReader implements PreferencesReader {

    /**
     * Returns and instance of this class.
     *
     * @param <U> user type
     * @param <I> item type
     * @return an instance of SimpleBinaryPreferencesReader
     */
    public static <U, I> SimpleBinaryPreferencesReader get() {
        return new SimpleBinaryPreferencesReader();
    }

    private SimpleBinaryPreferencesReader() {
    }

    @Override
    public <U, I> Stream<Tuple3<U, I, Double>> read(InputStream in, Parser<U> up, Parser<I> ip) throws IOException {
        return new BufferedReader(new InputStreamReader(in)).lines().map(line -> {
            CharSequence[] tokens = split(line, '\t', 3);
            U user = up.parse(tokens[0]);
            I item = ip.parse(tokens[1]);

            return Tuple.tuple(user, item, 1.0);
        });
    }

}
