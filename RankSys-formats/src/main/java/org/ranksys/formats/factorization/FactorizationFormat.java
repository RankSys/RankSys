package org.ranksys.formats.factorization;

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.mf.Factorization;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public interface FactorizationFormat {

    /**
     * Loads a matrix from a compressed input stream.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param in input stream
     * @param uIndex fast user index
     * @param iIndex fast item index
     * @return a factorization
     * @throws IOException when IO error
     */
    public <U, I> Factorization<U, I> load(InputStream in, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) throws IOException;

    /**
     * Saves this matrix factorization to a compressed output stream.
     *
     * @param <U> type of the users
     * @param <I> type of the items
     * @param factorization factorization
     * @param out output stream
     * @throws IOException when IO error
     */
    public <U, I> void save(Factorization<U, I> factorization, OutputStream out) throws IOException;
    
}
