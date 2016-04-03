/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.rec;

import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.pdp;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import static org.jooq.lambda.tuple.Tuple.tuple;

/**
 * Simple format for recommendations: tab-separated user-item-score triplets, grouping first by user (not necessarily in order) and then by decreasing order of score.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class SimpleRecommendationFormat<U, I> extends TuplesRecommendationFormat<U, I> {

    /**
     * Constructor.
     *
     * @param uParser user type parser
     * @param iParser item type parser
     */
    public SimpleRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        super((u, i, v, r) -> String.join("\t", u.toString(), i.toString(), Double.toString(v)),
                line -> {
                    CharSequence[] tokens = split(line, '\t', 4);
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[1]);
                    double v = pdp.applyAsDouble(tokens[2]);

                    return tuple(u, i, v);
                });
    }
}
