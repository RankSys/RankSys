/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.preference;

import org.ranksys.core.preference.fast.FastPreferenceData;
import org.ranksys.core.preference.fast.TransposedPreferenceData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.stream.Stream;
import java.util.function.BiConsumer;
import org.jooq.lambda.Unchecked;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;
import org.ranksys.core.util.tuples.Tuple2io;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Compression-ready format for rating preference data.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class CompressibleRatingPreferencesFormat {

    /**
     * Gets an instance of this class.
     *
     * @return instance of this class
     */
    public static CompressibleRatingPreferencesFormat get() {
        return new CompressibleRatingPreferencesFormat();
    }
    
        /**
     * Saves a PreferenceData instance in two files for user and item preferences, respectively. The format of the user preferences file consists on one list per line, starting with the identifier of the user followed by the identifier-rating pairs of the items related to that. The item preferences file follows the same format by swapping the roles of users and items.
     *
     * @param prefData preferences
     * @param up path to user preferences file
     * @param ip path to item preferences file
     * @throws FileNotFoundException one of the files could not be created
     * @throws IOException other IO error
     */
    public void write(FastPreferenceData<?, ?> prefData, String up, String ip) throws FileNotFoundException, IOException {
        CompressibleRatingPreferencesFormat.this.write(prefData, new FileOutputStream(up), new FileOutputStream(ip));
    }

    /**
     * Saves a PreferenceData instance in two files for user and item preferences, respectively. The format of the user preferences stream consists on one list per line, starting with the identifier of the user followed by the identifier-rating pairs of the items related to that. The item preferences stream follows the same format by swapping the roles of users and items.
     *
     * @param prefData preferences
     * @param uo stream of user preferences
     * @param io stream of user preferences
     * @throws IOException when IO error
     */
    public void write(FastPreferenceData<?, ?> prefData, OutputStream uo, OutputStream io) throws IOException {
        BiConsumer<FastPreferenceData<?, ?>, OutputStream> saver = Unchecked.biConsumer((prefs, os) -> {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                prefs.getUidxWithPreferences().forEach(Unchecked.intConsumer(uidx -> {
                    String a = prefs.getUidxPreferences(uidx)
                            .sorted((p1, p2) -> Integer.compare(p1.v1, p2.v1))
                            .map(p -> p.v1 + "\t" + (int) p.v2)
                            .collect(joining("\t"));

                    writer.write(uidx + "\t" + a);
                    writer.newLine();
                }));
            }
        });

        saver.accept(prefData, uo);
        saver.accept(new TransposedPreferenceData<>(prefData), io);
    }

    /**
     * Reads a file that complies with this format.
     *
     * @param in path to file
     * @return stream of user/item to preferences
     * @throws FileNotFoundException when file does not exist
     */
    public Stream<Tuple2io<int[][]>> read(String in) throws FileNotFoundException {
        return read(new FileInputStream(in));
    }
    
    /**
     * Reads an input stream that complies with this format.
     *
     * @param in input stream
     * @return stream of user/item to preferences
     */
    public Stream<Tuple2io<int[][]>> read(InputStream in) {
        return new BufferedReader(new InputStreamReader(in)).lines().map(line -> {
            String[] tokens = line.split("\t");
            int len = tokens.length / 2;
            int k = parseInt(tokens[0]);
            int[] idxs = new int[len];
            int[] vs = new int[len];
            for (int i = 0; i < len; i++) {
                idxs[i] = parseInt(tokens[2 * i + 1]);
                vs[i] = parseInt(tokens[2 * i + 2]);
            }
            return tuple(k, new int[][]{idxs, vs});
        });
    }

}
