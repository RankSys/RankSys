/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.factorization;

import org.ranksys.fast.index.FastItemIndex;
import org.ranksys.fast.index.FastUserIndex;
import org.ranksys.mf.Factorization;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Matrix factorisation format.
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
