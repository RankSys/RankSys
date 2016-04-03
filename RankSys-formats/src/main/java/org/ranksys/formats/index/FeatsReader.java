package org.ranksys.formats.index;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.ranksys.formats.parsing.Parser;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class FeatsReader {

    public static <F> Stream<F> read(String path, Parser<F> fp) throws IOException {
        return read(new FileInputStream(path), fp);
    }

    public static <F> Stream<F> read(InputStream in, Parser<F> fp) throws IOException {
        return Utils.readElemens(in, fp);
    }
}
