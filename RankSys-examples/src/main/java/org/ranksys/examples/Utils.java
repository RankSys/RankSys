/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.examples;

import es.uam.eps.ir.ranksys.core.feature.SimpleFeatureData.FeatureDataTuple;
import es.uam.eps.ir.ranksys.core.preference.SimplePreferenceData.PreferenceDataTuple;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Double.parseDouble;
import java.util.stream.Stream;
import java.util.function.Function;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import static java.lang.Long.parseLong;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class Utils {

    private static <T> Stream<T> readElemens(InputStream in, Function<CharSequence, T> parser) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines()
                    .map(line -> split(line, '\t', 2)[0])
                    .map(parser)
                    .sorted();
        }
    }
    
    public static Stream<Long> readUsers(InputStream in) throws IOException {
        return readElemens(in, cs -> parseLong(cs.toString()));
    }

    public static Stream<Long> readItems(InputStream in) throws IOException {
        return readElemens(in, cs -> parseLong(cs.toString()));
    }

    public static Stream<String> readFeatures(InputStream in) throws IOException {
        return readElemens(in, CharSequence::toString);
    }

    public static Stream<PreferenceDataTuple<Long, Long>> readPreferenceTuples(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().map(line -> {
                CharSequence[] tokens = split(line, '\t', 4);
                Long user = parseLong(tokens[0].toString());
                Long item = parseLong(tokens[1].toString());
                double value = parseDouble(tokens[2].toString());

                return new PreferenceDataTuple<>(user, item, value);
            });
        }
    }

    public static Stream<FeatureDataTuple<Long, String, Double>> readFeatureTuples(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().map(line -> {
                String[] tokens = line.split("\t", 3);
                Long item = parseLong(tokens[0]);
                String feat = tokens[1];
                double value = 1.0;

                return new FeatureDataTuple<>(item, feat, value);
            });
        }
    }

}
