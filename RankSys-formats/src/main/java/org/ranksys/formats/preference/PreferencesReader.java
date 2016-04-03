package org.ranksys.formats.preference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Double.parseDouble;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import java.io.FileInputStream;
import org.ranksys.formats.parsing.Parser;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class PreferencesReader {

    public static <U, I> Stream<Tuple3<U, I, Double>> readRating(String in, Parser<U> up, Parser<I> ip) throws IOException {
        return readRating(new FileInputStream(in), up, ip);
    }

    public static <U, I> Stream<Tuple3<U, I, Double>> readRating(InputStream in, Parser<U> up, Parser<I> ip) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().map(line -> {
                CharSequence[] tokens = split(line, '\t', 4);
                U user = up.parse(tokens[0]);
                I item = ip.parse(tokens[1]);
                double value = parseDouble(tokens[2].toString());

                return Tuple.tuple(user, item, value);
            });
        }
    }

    public static <U, I> Stream<Tuple3<U, I, Double>> readBinary(String in, Parser<U> up, Parser<I> ip) throws IOException {
        return readBinary(new FileInputStream(in), up, ip);
    }

    public static <U, I> Stream<Tuple3<U, I, Double>> readBinary(InputStream in, Parser<U> up, Parser<I> ip) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().map(line -> {
                CharSequence[] tokens = split(line, '\t', 3);
                U user = up.parse(tokens[0]);
                I item = ip.parse(tokens[1]);

                return Tuple.tuple(user, item, 1.0);
            });
        }
    }
}
