/* 
 * Copyright (C) 2015 RankSys http://ranksys.github.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ranksys.compression.preferences;

import org.ranksys.compression.codecs.CODEC;
import es.uam.eps.ir.ranksys.core.util.iterators.ArrayDoubleIterator;
import es.uam.eps.ir.ranksys.core.util.iterators.ArrayIntIterator;
import static org.ranksys.compression.util.Delta.atled;
import static org.ranksys.compression.util.Delta.delta;
import static es.uam.eps.ir.ranksys.core.util.parsing.IntParser.dip;
import es.uam.eps.ir.ranksys.fast.IdxObject;
import es.uam.eps.ir.ranksys.fast.preference.AbstractFastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.FasterPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
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
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import static java.util.stream.Collectors.joining;
import java.util.stream.IntStream;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import static java.util.stream.IntStream.of;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * PreferenceData for binary data using compression.
 * 
 * @param <U> type of users
 * @param <I> type of items
 * @param <Cu> coding for user identifiers
 * @param <Ci> coding for item identifiers
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class BinaryCODECPreferenceData<U, I, Cu, Ci> extends AbstractFastPreferenceData<U, I> implements FasterPreferenceData<U, I> {

    private final CODEC<Cu> u_codec;
    private final CODEC<Ci> i_codec;

    private final Cu[] u_idxs;
    private final int[] u_len;

    private final Ci[] i_idxs;
    private final int[] i_len;

    private final int numPreferences;

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

    private static Stream<IdxObject<int[]>> ul(FastPreferenceData<?, ?> preferences) {
        return preferences.getUidxWithPreferences().mapToObj(k -> {
            IdxPref[] pairs = preferences.getUidxPreferences(k)
                    .sorted((p1, p2) -> Integer.compare(p1.idx, p2.idx))
                    .toArray(n -> new IdxPref[n]);
            int[] idxs = new int[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                idxs[i] = pairs[i].idx;
            }
            return new IdxObject<>(k, idxs);
        });
    }

    private static Stream<IdxObject<int[]>> il(FastPreferenceData<?, ?> preferences) {
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
    @SuppressWarnings("unchecked")
    public BinaryCODECPreferenceData(Stream<IdxObject<int[]>> ul, Stream<IdxObject<int[]>> il, FastUserIndex<U> users, FastItemIndex<I> items, CODEC<Cu> u_codec, CODEC<Ci> i_codec) {
        super(users, items);

        this.u_codec = u_codec;
        this.i_codec = i_codec;

        u_idxs = (Cu[]) new Object[users.numUsers()];
        u_len = new int[users.numUsers()];
        index(ul, u_idxs, u_len, u_codec);

        i_idxs = (Ci[]) new Object[items.numItems()];
        i_len = new int[items.numItems()];
        index(il, i_idxs, i_len, i_codec);

        this.numPreferences = of(u_len).sum();
    }

    /**
     * Internal constructor for de-serialization.
     *
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @param u_idxs user preferences identifiers
     * @param u_len user preferences lengths
     * @param i_idxs item preferences identifiers
     * @param i_len item preferences lengths
     * @param numPreferences number of preferences
     * @param users user index
     * @param items item index
     */
    protected BinaryCODECPreferenceData(CODEC<Cu> u_codec, CODEC<Ci> i_codec, Cu[] u_idxs, int[] u_len, Ci[] i_idxs, int[] i_len, int numPreferences, FastUserIndex<U> users, FastItemIndex<I> items) {
        super(users, items);
        this.u_codec = u_codec;
        this.i_codec = i_codec;
        this.u_idxs = u_idxs;
        this.u_len = u_len;
        this.i_idxs = i_idxs;
        this.i_len = i_len;
        this.numPreferences = numPreferences;
    }

    private static <Cx> void index(Stream<IdxObject<int[]>> lists, Cx[] idxs, int[] lens, CODEC<Cx> x_codec) {
        lists.parallel().forEach(list -> {
            int k = list.idx;
            int[] _idxs = list.v;

            lens[k] = _idxs.length;
            if (!x_codec.isIntegrated()) {
                delta(_idxs, 0, _idxs.length);
            }
            idxs[k] = x_codec.co(_idxs, 0, _idxs.length);
        });
    }

    @Override
    public int numUsers(int iidx) {
        return i_len[iidx];
    }

    @Override
    public int numItems(int uidx) {
        return u_len[uidx];
    }

    @Override
    public int numPreferences() {
        return numPreferences;
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(final int uidx) {
        return getPreferences(u_idxs[uidx], u_len[uidx], u_codec);
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(final int iidx) {
        return getPreferences(i_idxs[iidx], i_len[iidx], i_codec);
    }

    @Override
    public IntIterator getUidxIidxs(final int uidx) {
        return getIdx(u_idxs[uidx], u_len[uidx], u_codec);
    }

    @Override
    public DoubleIterator getUidxVs(final int uidx) {
        double[] vs = new double[u_len[uidx]];
        Arrays.fill(vs, 1.0);
        return new ArrayDoubleIterator(vs);
    }

    @Override
    public IntIterator getIidxUidxs(final int iidx) {
        return getIdx(i_idxs[iidx], i_len[iidx], i_codec);
    }

    @Override
    public DoubleIterator getIidxVs(final int iidx) {
        double[] vs = new double[i_len[iidx]];
        Arrays.fill(vs, 1.0);
        return new ArrayDoubleIterator(vs);
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
        return range(0, len).mapToObj(i -> pref.refill(idxs[i]));
    }

    private static <Cx> IntIterator getIdx(Cx cidxs, int len, CODEC<Cx> x_codec) {
        if (len == 0) {
            return IntIterators.EMPTY_ITERATOR;
        }
        int[] idxs = new int[len];
        x_codec.dec(cidxs, idxs, 0, len);
        if (!x_codec.isIntegrated()) {
            atled(idxs, 0, len);
        }
        return new ArrayIntIterator(idxs);
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return range(0, u_len.length).filter(uidx -> u_len[uidx] > 0);
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return range(0, i_len.length).filter(iidx -> i_len[iidx] > 0);
    }

    /**
     * Reads two files for user and item preferences and builds a compressed
     * PreferenceData. The format of the user preferences stream consists on one
     * list per line, starting with the identifier of the user followed by the
     * identifiers of the items related to that. The item preferences stream
     * follows the same format by swapping the roles of users and items.
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
     * Reads two streams for user and item preferences and builds a compressed
     * PreferenceData. The format of the user preferences stream consists on one
     * list per line, starting with the identifier of the user followed by the
     * identifiers of the items related to that. The item preferences stream
     * follows the same format by swapping the roles of users and items.
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
        Function<InputStream, Stream<IdxObject<int[]>>> reader = is -> {
            return new BufferedReader(new InputStreamReader(is)).lines().map(line -> {
                String[] tokens = line.split("\t");
                int len = tokens.length - 1;
                int k = dip.parse(tokens[0]);
                int[] idxs = new int[len];
                for (int i = 0; i < len; i++) {
                    idxs[i] = dip.parse(tokens[i + 1]);
                }
                return new IdxObject<>(k, idxs);
            });
        };

        Stream<IdxObject<int[]>> ul = reader.apply(uo);
        Stream<IdxObject<int[]>> il = reader.apply(io);

        return new BinaryCODECPreferenceData<>(ul, il, users, items, u_codec, i_codec);
    }

    /**
     * Saves a PreferenceData instance in two files for user and item
     * preferences, respectively. The format of the user preferences file
     * consists on one list per line, starting with the identifier of the user
     * followed by the identifiers of the items related to that. The item 
     * preferences file follows the same format by swapping the roles of 
     * users and items.
     *
     * @param prefData preferences
     * @param up path to user preferences file
     * @param ip path to item preferences file
     * @throws FileNotFoundException one of the files could not be created
     * @throws IOException other IO error
     */
    public static void save(FastPreferenceData<?, ?> prefData, String up, String ip) throws FileNotFoundException, IOException {
        save(prefData, new FileOutputStream(up), new FileOutputStream(ip));
    }

    /**
     * Saves a PreferenceData instance in two files for user and item
     * preferences, respectively. The format of the user preferences stream
     * consists on one list per line, starting with the identifier of the user
     * followed by the identifiers of the items related to that. The item 
     * preferences stream follows the same format by swapping the roles of 
     * users and items.
     *
     * @param prefData preferences
     * @param uo stream of user preferences
     * @param io stream of user preferences
     * @throws IOException when IO error
     */
    public static void save(FastPreferenceData<?, ?> prefData, OutputStream uo, OutputStream io) throws IOException {
        BiConsumer<FastPreferenceData<?, ?>, OutputStream> saver = (prefs, os) -> {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                prefs.getUidxWithPreferences().forEach(uidx -> {
                    String a = prefs.getUidxPreferences(uidx)
                            .sorted((p1, p2) -> Integer.compare(p1.idx, p2.idx))
                            .map(p -> Integer.toString(p.idx))
                            .collect(joining("\t"));
                    try {
                        writer.write(uidx + "\t" + a);
                        writer.newLine();
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                });
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        };

        saver.accept(prefData, uo);
        saver.accept(new TransposedPreferenceData<>(prefData), io);
    }

    /**
     * Serializes this instance by writing it into a compressed binary file.
     *
     * @param path path to compressed binary file
     * @throws IOException when IO error
     */
    public void serialize(String path) throws IOException {
        try (GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(path));
                ObjectOutputStream out = new ObjectOutputStream(os)) {
            out.writeObject(u_idxs);
            out.writeObject(u_len);
            out.writeObject(i_idxs);
            out.writeObject(i_len);
            out.writeInt(numPreferences);
            out.writeObject(ui);
            out.writeObject(ii);
        }
    }

    /**
     * De-serializes a compressed binary file to a PrefereceData instance.
     *
     * @param <U> type of user
     * @param <I> type of item
     * @param <Cu> coding for user preferences
     * @param <Ci> coding for item preferences
     * @param path path to compressed binary file
     * @param u_codec user preferences list CODEC
     * @param i_codec item preferences list CODEC
     * @return de-serialized PreferenceData instance
     * @throws IOException when IO error
     * @throws ClassNotFoundException when reading a bad binary file
     */
    @SuppressWarnings("unchecked")
    public static <U, I, Cu, Ci> BinaryCODECPreferenceData<U, I, Cu, Ci> deserialize(String path, CODEC<Cu> u_codec, CODEC<Ci> i_codec) throws IOException, ClassNotFoundException {
        Cu[] u_idxs;
        int[] u_len;
        Ci[] i_idxs;
        int[] i_len;
        int numPreferences;
        FastUserIndex<U> users;
        FastItemIndex<I> items;

        try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(path));
                ObjectInputStream in = new ObjectInputStream(is)) {
            u_idxs = (Cu[]) in.readObject();
            u_len = (int[]) in.readObject();
            i_idxs = (Ci[]) in.readObject();
            i_len = (int[]) in.readObject();
            numPreferences = in.readInt();
            users = (FastUserIndex<U>) in.readObject();
            items = (FastItemIndex<I>) in.readObject();
        }

        return new BinaryCODECPreferenceData<>(u_codec, i_codec, u_idxs, u_len, i_idxs, i_len, numPreferences, users, items);
    }

}
