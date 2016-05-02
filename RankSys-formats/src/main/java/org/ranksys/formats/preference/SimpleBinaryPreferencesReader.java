package org.ranksys.formats.preference;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parser;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleBinaryPreferencesReader implements PreferencesReader {

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
