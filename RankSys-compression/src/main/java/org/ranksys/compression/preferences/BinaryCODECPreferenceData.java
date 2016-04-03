/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.preferences;

import org.ranksys.compression.codecs.CODEC;
import static org.ranksys.compression.util.Delta.atled;
import static org.ranksys.compression.util.Delta.delta;
import static es.uam.eps.ir.ranksys.core.util.parsing.IntParser.dip;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Arrays;
import static java.util.Comparator.comparingInt;
import org.ranksys.core.util.iterators.ArrayDoubleIterator;
import org.ranksys.core.util.tuples.Tuple2io;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * PreferenceData for binary data using compression.
 * <br>
 * If you use this code, please cite the following papers:
 * <ul>
 * <li>Vargas, S., Macdonald, C., Ounis, I. (2015). Analysing Compression Techniques for In-Memory Collaborative Filtering. In Poster Proceedings of the 9th ACM Conference on Recommender Systems. <a href="http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf">http://ceur-ws.org/Vol-1441/recsys2015_poster2.pdf</a>.</li>
 * <li>Catena, M., Macdonald, C., Ounis, I. (2014). On Inverted Index Compression for Search Engine Efficiency. In ECIR (pp. 359–371). doi:10.1007/978-3-319-06028-6_30</li>
 * </ul>
 * The code that reproduces the results of the RecSys 2015 poster by Vargas et al. in a separated project: <a href="http://github.com/saulvargas/recsys2015">http://github.com/saulvargas/recsys2015</a>
 * <br>
 * The search index compression technologies of the ECIR paper by Catena et al. is part of the Terrier IR Platform: <a href="http://terrier.org/docs/v4.0/compression.html">http://terrier.org/docs/v4.0/compression.html</a>.
 *
 * @param <U> type of users
 * @param <I> type of items
 * @param <Cu> coding for user identifiers
 * @param <Ci> coding for item identifiers
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class BinaryCODECPreferenceData<U, I, Cu, Ci> extends AbstractCODECPreferenceData<U, I, Cu, Ci> {

    /**
     * Constructor that utilizes other PreferenceData object.
     *
     * @param preferences input preference data to be copied
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     */
    public BinaryCODECPreferenceData(FastPreferenceData<U, I> preferences, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec) {
        this(ul(preferences), il(preferences), users, items, u_codec, i_codec);
    }

    private static Stream<Tuple2io<int[]>> ul(FastPreferenceData<?, ?> preferences) {
        return preferences.getUidxWithPreferences().mapToObj(k -> {
            IdxPref[] pairs = preferences.getUidxPreferences(k)
                    .sorted(comparingInt(IdxPref::v1))
                    .toArray(n -> new IdxPref[n]);
            int[] idxs = new int[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                idxs[i] = pairs[i].v1;
            }
            return tuple(k, idxs);
        });
    }

    private static Stream<Tuple2io<int[]>> il(FastPreferenceData<?, ?> preferences) {
        return ul(new TransposedPreferenceData<>(preferences));
    }

    /**
     * Constructor using streams of user and items preferences lists.
     *
     * @param ul stream of user preferences lists
     * @param il stream of item preferences lists
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     */
    public BinaryCODECPreferenceData(Stream<Tuple2io<int[]>> ul, Stream<Tuple2io<int[]>> il, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec) {
        super(users, items, u_codec, i_codec);

        index(ul, u_idxs, u_len, u_codec);
        index(il, i_idxs, i_len, i_codec);
    }

    private static <Cx> void index(Stream<Tuple2io<int[]>> lists, Cx[] idxs, int[] lens, CODEC<Cx> x_codec) {
        lists.parallel().forEach(list -> {
            int k = list.v1;
            int[] _idxs = list.v2;

            lens[k] = _idxs.length;
            if (!x_codec.isIntegrated()) {
                delta(_idxs, 0, _idxs.length);
            }
            idxs[k] = x_codec.co(_idxs, 0, _idxs.length);
        });
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(final int uidx) {
        return getPreferences(u_idxs[uidx], u_len[uidx], u_codec);
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(final int iidx) {
        return getPreferences(i_idxs[iidx], i_len[iidx], i_codec);
    }

    private static <Cx> Stream<IdxPref> getPreferences(Cx cidxs, int len, CODEC<Cx> x_codec) {
        if (len == 0) {
            return Stream.empty();
        }
        IdxPref pref = new IdxPref(-1, 1.0);
        int[] idxs = new int[len];
        x_codec.dec(cidxs, idxs, 0, len);
        if (!x_codec.isIntegrated()) {
            atled(idxs, 0, len);
        }
        return range(0, len).mapToObj(i -> new IdxPref(idxs[i], 1.0));
    }

    @Override
    public DoubleIterator getUidxVs(final int uidx) {
        double[] vs = new double[u_len[uidx]];
        Arrays.fill(vs, 1.0);
        return new ArrayDoubleIterator(vs);
    }

    @Override
    public DoubleIterator getIidxVs(final int iidx) {
        double[] vs = new double[i_len[iidx]];
        Arrays.fill(vs, 1.0);
        return new ArrayDoubleIterator(vs);
    }

    /**
     * Reads two files for user and item preferences and builds a compressed PreferenceData. The format of the user preferences stream consists on one list per line, starting with the identifier of the user followed by the identifiers of the items related to that. The item preferences stream follows the same format by swapping the roles of users and items.
     *
     * @param <U> type of users
     * @param <I> type of items
     * @param <Cu> coding for user identifiers
     * @param <Ci> coding for item identifiers
     * @param up path to user preferences file
     * @param ip path to item preferences file
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @return compressed preference data
     * @throws FileNotFoundException when one of the files does not exist
     */
    public static <U, I, Cu, Ci> BinaryCODECPreferenceData<U, I, Cu, Ci> load(String up, String ip, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec) throws FileNotFoundException {
        return load(new FileInputStream(up), new FileInputStream(ip), users, items, u_codec, i_codec);
    }

    /**
     * Reads two streams for user and item preferences and builds a compressed PreferenceData. The format of the user preferences stream consists on one list per line, starting with the identifier of the user followed by the identifiers of the items related to that. The item preferences stream follows the same format by swapping the roles of users and items.
     *
     * @param <U> type of users
     * @param <I> type of items
     * @param <Cu> coding for user identifiers
     * @param <Ci> coding for item identifiers
     * @param uo stream user preferences
     * @param io stream item preferences
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @return compressed preference data
     */
    public static <U, I, Cu, Ci> BinaryCODECPreferenceData<U, I, Cu, Ci> load(InputStream uo, InputStream io, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec) {
        Function<InputStream, Stream<Tuple2io<int[]>>> reader = is -> {
            return new BufferedReader(new InputStreamReader(is)).lines().map(line -> {
                String[] tokens = line.split("\t");
                int len = tokens.length - 1;
                int k = dip.parse(tokens[0]);
                int[] idxs = new int[len];
                for (int i = 0; i < len; i++) {
                    idxs[i] = dip.parse(tokens[i + 1]);
                }
                return tuple(k, idxs);
            });
        };

        Stream<Tuple2io<int[]>> ul = reader.apply(uo);
        Stream<Tuple2io<int[]>> il = reader.apply(io);

        return new BinaryCODECPreferenceData<>(ul, il, users, items, u_codec, i_codec);
    }
}
