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
public class ItemsReader {

    public static <I> Stream<I> read(String path, Parser<I> ip) throws IOException {
        return read(new FileInputStream(path), ip);
    }

    public static <I> Stream<I> read(InputStream in, Parser<I> ip) throws IOException {
        return Utils.readElemens(in, ip);
    }
}
