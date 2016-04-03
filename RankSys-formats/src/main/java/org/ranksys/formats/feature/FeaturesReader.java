package org.ranksys.formats.feature;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parser;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public interface FeaturesReader {

    public default <I, F> Stream<Tuple3<I, F, Double>> read(String in, Parser<I> ip, Parser<F> fp) throws IOException {
        return read(new FileInputStream(in), ip, fp);

    }

    public <I, F> Stream<Tuple3<I, F, Double>> read(InputStream in, Parser<I> ip, Parser<F> fp) throws IOException;

}
