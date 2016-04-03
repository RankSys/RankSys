package org.ranksys.formats.preference;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
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
import java.util.stream.Stream;
import java.util.function.BiConsumer;
import org.jooq.lambda.Unchecked;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;
import org.ranksys.core.util.tuples.Tuple2io;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class CompressibleBinaryPreferencesFormat {

    public static CompressibleBinaryPreferencesFormat get() {
        return new CompressibleBinaryPreferencesFormat();
    }

    /**
     * Saves a PreferenceData instance in two files for user and item preferences, respectively. The format of the user preferences file consists on one list per line, starting with the identifier of the user followed by the identifiers of the items related to that. The item preferences file follows the same format by swapping the roles of users and items.
     *
     * @param prefData preferences
     * @param up path to user preferences file
     * @param ip path to item preferences file
     * @throws FileNotFoundException one of the files could not be created
     * @throws IOException other IO error
     */
    public <U, I> void write(FastPreferenceData<?, ?> prefData, String up, String ip) throws FileNotFoundException, IOException {
        write(prefData, new FileOutputStream(up), new FileOutputStream(ip));
    }

    /**
     * Saves a PreferenceData instance in two files for user and item preferences, respectively. The format of the user preferences stream consists on one list per line, starting with the identifier of the user followed by the identifiers of the items related to that. The item preferences stream follows the same format by swapping the roles of users and items.
     *
     * @param prefData preferences
     * @param uo stream of user preferences
     * @param io stream of user preferences
     * @throws IOException when IO error
     */
    public <U, I> void write(FastPreferenceData<?, ?> prefData, OutputStream uo, OutputStream io) throws IOException {
        BiConsumer<FastPreferenceData<?, ?>, OutputStream> saver = Unchecked.biConsumer((prefs, os) -> {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                prefs.getUidxWithPreferences().forEach(Unchecked.intConsumer(uidx -> {
                    String a = prefs.getUidxPreferences(uidx)
                            .mapToInt(IdxPref::v1)
                            .sorted()
                            .mapToObj(Integer::toString)
                            .collect(joining("\t"));

                    writer.write(uidx + "\t" + a);
                    writer.newLine();
                }));
            }
        });

        saver.accept(prefData, uo);
        saver.accept(new TransposedPreferenceData<>(prefData), io);
    }

    public Stream<Tuple2io<int[]>> read(String in) throws FileNotFoundException {
        return read(new FileInputStream(in));
    }

    public Stream<Tuple2io<int[]>> read(InputStream in) {
        return new BufferedReader(new InputStreamReader(in)).lines().map(line -> {
            String[] tokens = line.split("\t");
            int len = tokens.length - 1;
            int k = parseInt(tokens[0]);
            int[] idxs = new int[len];
            for (int i = 0; i < len; i++) {
                idxs[i] = parseInt(tokens[i + 1]);
            }
            
            return tuple(k, idxs);
        });
    }

}
