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
public class UsersReader {

    public static <U> Stream<U> read(String path, Parser<U> up) throws IOException {
        return read(new FileInputStream(path), up);
    }

    public static <U> Stream<U> read(InputStream in, Parser<U> up) throws IOException {
        return Utils.readElemens(in, up);
    }

}
