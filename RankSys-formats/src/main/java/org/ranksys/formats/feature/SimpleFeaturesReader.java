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

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFeaturesReader implements FeaturesReader {

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

            return Tuple.tuple(item, feat, 1.0);
        });
    }
}
