/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.factorization;

import org.ranksys.core.index.fast.FastItemIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ranksys.fm.PreferenceFM;

/**
 * Factorisation machine format for reading and writing from files.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public interface FMFormat {

    /**
     * Loads a factorisation machine from an input stream.
     *
     * @param <U> type of users
     * @param <I> type of items
     * @param in input stream
     * @param users user index
     * @param items item index
     * @return factorisation machine for recommendation tasks
     * @throws IOException when I/O problems
     */
    public <U, I> PreferenceFM load(InputStream in, FastUserIndex<U> users, FastItemIndex<I> items) throws IOException;

    /**
     * Saves a factorisation machine in an output stream.
     *
     * @param <U> user type
     * @param <I> item type
     * @param fm factorisation machine for recommendation tasks
     * @param out output stream where the factorisation machine is written
     * @throws IOException when I/O problems
     */
    public <U, I> void save(PreferenceFM fm, OutputStream out) throws IOException;

}
