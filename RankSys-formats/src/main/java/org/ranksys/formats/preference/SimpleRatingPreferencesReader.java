package org.ranksys.formats.preference;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Double.parseDouble;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parser;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleRatingPreferencesReader implements PreferencesReader {

    public static <U, I> SimpleRatingPreferencesReader get() {
        return new SimpleRatingPreferencesReader();
    }

    private SimpleRatingPreferencesReader() {
    }

    @Override
    public <U, I> Stream<Tuple3<U, I, Double>> read(InputStream in, Parser<U> up, Parser<I> ip) throws IOException {
        return new BufferedReader(new InputStreamReader(in)).lines().map(line -> {
            CharSequence[] tokens = split(line, '\t', 4);
            U user = up.parse(tokens[0]);
            I item = ip.parse(tokens[1]);
            double value = parseDouble(tokens[2].toString());

            return Tuple.tuple(user, item, value);
        });
    }

}
