/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.rec;

import static org.ranksys.core.util.FastStringSplitter.split;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Double.NaN;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import static org.jooq.lambda.tuple.Tuple.tuple;
import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.pdp;

/**
 * Format similar to SimpleRecommendationFormat, but files are zipped and scores can be omitted when writing and reading for higher performance when they are not needed and only order matters.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <U> user type
 * @param <I> item type
 */
public class ZipRecommendationFormat<U, I> extends TuplesRecommendationFormat<U, I> {

    /**
     * Constructor.
     *
     * @param uParser user parser
     * @param iParser item parser
     * @param ignoreScores should scores be read or written?
     */
    public ZipRecommendationFormat(Parser<U> uParser, Parser<I> iParser, boolean ignoreScores) {
        this(uParser, iParser, ignoreScores, false);
    }

    /**
     * Constructor.
     *
     * @param uParser user parser
     * @param iParser item parser
     * @param ignoreScores should scores be read or written?
     * @param sortByDecreasingScore sort read tuples by decreasing score?
     */
    public ZipRecommendationFormat(Parser<U> uParser, Parser<I> iParser, boolean ignoreScores, boolean sortByDecreasingScore) {
        super(
                (u, i, v, r) -> {
                    if (ignoreScores) {
                        return String.join("\t", u.toString(), i.toString());
                    } else {
                        return String.join("\t", u.toString(), i.toString(), Double.toString(v));
                    }
                },
                line -> {
                    CharSequence[] tokens = split(line, '\t', ignoreScores ? 3 : 4);
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[1]);
                    double v = ignoreScores ? NaN : pdp.applyAsDouble(tokens[2]);

                    return tuple(u, i, v);
                },
                sortByDecreasingScore
        );
    }

    @Override
    public RecommendationFormat.Writer<U, I> getWriter(OutputStream out) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(out);
        zip.putNextEntry(new ZipEntry("recommendation"));
        return new Writer(zip);
    }

    @Override
    public RecommendationFormat.Reader<U, I> getReader(InputStream in) throws IOException {
        ZipInputStream zip = new ZipInputStream(in);
        zip.getNextEntry();
        return new Reader(zip);
    }
}
