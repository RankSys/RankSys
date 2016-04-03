package org.ranksys.formats.preference;

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
public interface PreferencesReader {

    public default <U, I> Stream<Tuple3<U, I, Double>> read(String in, Parser<U> up, Parser<I> ip) throws IOException {
        return read(new FileInputStream(in), up, ip);
    }

    public <U, I> Stream<Tuple3<U, I, Double>> read(InputStream in, Parser<U> up, Parser<I> ip) throws IOException;

}
