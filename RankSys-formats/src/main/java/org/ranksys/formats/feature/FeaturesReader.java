/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.feature;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parser;

/**
 * Reader of files containing information to load into a FeatureData object.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public interface FeaturesReader {

    /**
     * Reads item-feature-value triples from a file.
     *
     * @param <I> item type
     * @param <F> feat type
     * @param in path to file
     * @param ip item parser
     * @param fp feat parser
     * @return stream of item-feature-value triples
     * @throws IOException when I/O problems
     */
    public default <I, F> Stream<Tuple3<I, F, Double>> read(String in, Parser<I> ip, Parser<F> fp) throws IOException {
        return read(new FileInputStream(in), ip, fp);

    }

    /**
     * Reads item-feature-value triples from an input stream.
     *
     * @param <I> item type
     * @param <F> feat type
     * @param in input stream to read
     * @param ip item parser
     * @param fp feat parser
     * @return stream of item-feature-value triples
     * @throws IOException when I/O problems
     */
    public <I, F> Stream<Tuple3<I, F, Double>> read(InputStream in, Parser<I> ip, Parser<F> fp) throws IOException;

}
