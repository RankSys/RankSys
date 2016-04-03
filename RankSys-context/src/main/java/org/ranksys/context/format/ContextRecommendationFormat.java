/* 
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.context.format;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import java.util.function.Function;
import static org.jooq.lambda.tuple.Tuple.tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.pdp;
import org.ranksys.formats.rec.TuplesRecommendationFormat;

/**
 * User-item-value-context recommendation file writer and reader.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 *
 * @param <U> user type
 * @param <C> context type
 * @param <I> item type
 */
public class ContextRecommendationFormat<U, I, C> extends TuplesRecommendationFormat<Tuple2<U, C>, I> {

    /**
     * Constructor.
     *
     * @param up user parser
     * @param ip item parser
     * @param vp value parser
     * @param contextWriter context writer
     * @param contextReader context reader
     */
    public ContextRecommendationFormat(Parser<U> up, Parser<I> ip, Function<C, String> contextWriter, Function<String, C> contextReader) {
        super(
                (uc, i, v, r) -> {
                    return String.join("\t", uc.v1.toString(), i.toString(), 
                            Double.toString(v), contextWriter.apply(uc.v2));
                },
                line -> {
                    CharSequence[] tokens = split(line, '\t', 4);
                    U u = up.parse(tokens[0]);
                    I i = ip.parse(tokens[1]);
                    double v = pdp.applyAsDouble(tokens[2]);
                    C c = contextReader.apply(tokens[3].toString());
                    
                    return tuple(tuple(u, c), i, v);
                }
        );
    }

}
