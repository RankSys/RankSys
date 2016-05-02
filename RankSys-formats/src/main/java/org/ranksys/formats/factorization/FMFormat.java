package org.ranksys.formats.factorization;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ranksys.fm.PreferenceFM;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public interface FMFormat {

    public <U, I> PreferenceFM load(InputStream in, FastUserIndex<U> users, FastItemIndex<I> items) throws IOException;

    public <U, I> void save(PreferenceFM fm, OutputStream out) throws IOException;

}
