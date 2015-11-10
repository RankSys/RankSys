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
import es.uam.eps.ir.ranksys.fast.IdxObject;
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
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import org.ranksys.core.util.iterators.ArrayDoubleIterator;

/**
 * PreferenceData for rating data using compression.
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
 * @param <Cv> coding for ratings
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class RatingCODECPreferenceData<U, I, Cu, Ci, Cv> extends AbstractCODECPreferenceData<U, I, Cu, Ci> {

    private final CODEC<Cv> r_codec;

    private final Cv[] u_vs;

    private final Cv[] i_vs;

    /**
     * Constructor that utilizes other PreferenceData object.
     *
     * @param preferences input preference data to be copied
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @param r_codec ratings CODEC
     */
    public RatingCODECPreferenceData(FastPreferenceData<U, I> preferences, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec, CODEC<Cv> r_codec) {
        this(ul(preferences), il(preferences), users, items, u_codec, i_codec, r_codec);
    }

    private static Stream<IdxObject<int[][]>> ul(FastPreferenceData<?, ?> preferences) {
        return preferences.getUidxWithPreferences().mapToObj(k -> {
            IdxPref[] pairs = preferences.getUidxPreferences(k)
                    .sorted((p1, p2) -> Integer.compare(p1.idx, p2.idx))
                    .toArray(n -> new IdxPref[n]);
            int[] idxs = new int[pairs.length];
            int[] vs = new int[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                idxs[i] = pairs[i].idx;
                vs[i] = (int) pairs[i].v;
            }
            return new IdxObject<>(k, new int[][]{idxs, vs});
        });
    }

    private static Stream<IdxObject<int[][]>> il(FastPreferenceData<?, ?> preferences) {
        return ul(new TransposedPreferenceData<>(preferences));
    }

    /**
     * Constructor using streams of user and items preferences lists.
     *
     * @param ul stream of user preferences lists (id-rating)
     * @param il stream of item preferences lists (id-rating)
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @param r_codec ratings CODEC
     */
    @SuppressWarnings("unchecked")
    public RatingCODECPreferenceData(Stream<IdxObject<int[][]>> ul, Stream<IdxObject<int[][]>> il, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec, CODEC<Cv> r_codec) {
        super(users, items, u_codec, i_codec);

        this.r_codec = r_codec;
        this.u_vs = (Cv[]) new Object[users.numUsers()];
        this.i_vs = (Cv[]) new Object[items.numItems()];

        index(ul, u_idxs, u_vs, u_len, u_codec, r_codec);
        index(il, i_idxs, i_vs, i_len, i_codec, r_codec);
    }

    private static <Cx, Cv> void index(Stream<IdxObject<int[][]>> lists, Cx[] idxs, Cv[] vs, int[] lens, CODEC<Cx> x_codec, CODEC<Cv> r_codec) {
        lists.parallel().forEach(list -> {
            int k = list.idx;
            int[] _idxs = list.v[0];
            int[] _vs = list.v[1];

            lens[k] = _idxs.length;
            if (!x_codec.isIntegrated()) {
                delta(_idxs, 0, _idxs.length);
            }
            idxs[k] = x_codec.co(_idxs, 0, _idxs.length);
            vs[k] = r_codec.co(_vs, 0, _vs.length);
        });
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(final int uidx) {
        return getPreferences(u_idxs[uidx], u_vs[uidx], u_len[uidx], u_codec, r_codec);
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(final int iidx) {
        return getPreferences(i_idxs[iidx], i_vs[iidx], i_len[iidx], i_codec, r_codec);
    }

    private static <Cx, Cv> Stream<IdxPref> getPreferences(Cx cidxs, Cv cvs, int len, CODEC<Cx> x_codec, CODEC<Cv> r_codec) {
        IdxPref pref = new IdxPref();
        int[] idxs = new int[len];
        int[] vs = new int[len];
        x_codec.dec(cidxs, idxs, 0, len);
        r_codec.dec(cvs, vs, 0, len);
        if (!x_codec.isIntegrated()) {
            atled(idxs, 0, len);
        }
        return range(0, len).mapToObj(i -> pref.refill(idxs[i], vs[i]));
    }

    @Override
    public DoubleIterator getUidxVs(final int uidx) {
        return getVs(u_vs[uidx], u_len[uidx], r_codec);
    }

    @Override
    public DoubleIterator getIidxVs(final int iidx) {
        return getVs(i_vs[iidx], i_len[iidx], r_codec);
    }

    private static <Cv> DoubleIterator getVs(Cv cvs, int len, CODEC<Cv> r_codec) {
        if (len == 0) {
            return DoubleIterators.EMPTY_ITERATOR;
        }
        int[] vsi = new int[len];
        r_codec.dec(cvs, vsi, 0, len);
        double[] vsd = new double[len];
        for (int i = 0; i < len; i++) {
            vsd[i] = vsi[i];
        }
        return new ArrayDoubleIterator(vsd);
    }

    /**
     * Reads two files for user and item preferences and builds a compressed PreferenceData. The format of the user preferences stream consists on one list per line, starting with the identifier of the user followed by the identifier-rating pairs of the items related to that. The item preferences stream follows the same format by swapping the roles of users and items.
     *
     * @param <U> type of users
     * @param <I> type of items
     * @param <Cu> coding for user identifiers
     * @param <Ci> coding for item identifiers
     * @param <Cv> coding for ratings
     * @param up path to user preferences file
     * @param ip path to item preferences file
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @param r_codec ratings CODEC
     * @return compressed preference data
     * @throws FileNotFoundException when one of the files does not exist
     */
    public static <U, I, Cu, Ci, Cv> RatingCODECPreferenceData<U, I, Cu, Ci, Cv> load(String up, String ip, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec, CODEC<Cv> r_codec) throws FileNotFoundException {
        return load(new FileInputStream(up), new FileInputStream(ip), users, items, u_codec, i_codec, r_codec);
    }

    /**
     * Reads two streams for user and item preferences and builds a compressed PreferenceData. The format of the user preferences stream consists on one list per line, starting with the identifier of the user followed by the identifier-rating pairs of the items related to that. The item preferences stream follows the same format by swapping the roles of users and items.
     *
     * @param <U> type of users
     * @param <I> type of items
     * @param <Cu> coding for user identifiers
     * @param <Ci> coding for item identifiers
     * @param <Cv> coding for ratings
     * @param uo stream user preferences
     * @param io stream item preferences
     * @param users user index
     * @param items item index
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @param r_codec ratings CODEC
     * @return compressed preference data
     */
    public static <U, I, Cu, Ci, Cv> RatingCODECPreferenceData<U, I, Cu, Ci, Cv> load(InputStream uo, InputStream io, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec, CODEC<Cv> r_codec) {
        Function<InputStream, Stream<IdxObject<int[][]>>> reader = is -> {
            return new BufferedReader(new InputStreamReader(is)).lines().map(line -> {
                String[] tokens = line.split("\t");
                int len = tokens.length / 2;
                int k = dip.parse(tokens[0]);
                int[] idxs = new int[len];
                int[] vs = new int[len];
                for (int i = 0; i < len; i++) {
                    idxs[i] = dip.parse(tokens[2 * i + 1]);
                    vs[i] = dip.parse(tokens[2 * i + 2]);
                }
                return new IdxObject<>(k, new int[][]{idxs, vs});
            });
        };

        Stream<IdxObject<int[][]>> ul = reader.apply(uo);
        Stream<IdxObject<int[][]>> il = reader.apply(io);

        return new RatingCODECPreferenceData<>(ul, il, users, items, u_codec, i_codec, r_codec);
    }
}
