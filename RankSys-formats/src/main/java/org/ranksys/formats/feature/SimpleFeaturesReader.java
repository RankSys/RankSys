/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.feature;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parser;
import org.ranksys.formats.parsing.Parsers;

/**
 * Reads a file of tab-separated item-feat-value triples, one per line.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFeaturesReader implements FeaturesReader {

    /**
     * Returns and instance of this class.
     *
     * @param <I> item type
     * @param <F> feat type
     * @return an instance of SimpleFeaturesReader
     */
    public static <I, F> SimpleFeaturesReader get() {
        return new SimpleFeaturesReader();
    }

    private SimpleFeaturesReader() {
    }

    @Override
    public <I, F> Stream<Tuple3<I, F, Double>> read(String in, Parser<I> ip, Parser<F> fp) throws IOException {
        return read(new FileInputStream(in), ip, fp);
    }

    @Override
    public <I, F> Stream<Tuple3<I, F, Double>> read(InputStream in, Parser<I> ip, Parser<F> fp) throws IOException {
        return new BufferedReader(new InputStreamReader(in)).lines().map(line -> {
            String[] tokens = line.split("\t", 3);
            I item = ip.parse(tokens[0]);
            F feat = fp.parse(tokens[1]);
            double value = Parsers.dp.parse(tokens[2]);
            return Tuple.tuple(item, feat, value);
        });
    }
}
