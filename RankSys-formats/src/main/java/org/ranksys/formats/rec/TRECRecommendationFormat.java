/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.rec;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import org.ranksys.formats.parsing.Parser;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.ranksys.formats.parsing.Parsers.pdp;

/**
 * Reader for TREC-like recommendations.
 *
 * @param <U> user type
 * @param <I> item type
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class TRECRecommendationFormat<U, I> extends TuplesRecommendationFormat<U, I> {

    /**
     * Constructor.
     *
     * @param uParser user type parser
     * @param iParser item type parser
     */
    public TRECRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this(uParser, iParser, false);
    }

    /**
     * Constructor.
     *
     * @param uParser user type parser
     * @param iParser item type parser
     * @param sortByDecreasingScore sort read tuples by decreasing score?
     */
    public TRECRecommendationFormat(Parser<U> uParser, Parser<I> iParser, boolean sortByDecreasingScore) {
        super(
                (u, i, v, r) -> {
                    return String.join("\t", u.toString(), "Q0", i.toString(), Long.toString(r + 1), Double.toString(v), "r");
                },
                line -> {
                    CharSequence[] tokens = split(line, '\t', 6);
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[2]);
                    double v = pdp.applyAsDouble(tokens[4]);

                    return tuple(u, i, v);
                },
                sortByDecreasingScore
        );
    }
}
