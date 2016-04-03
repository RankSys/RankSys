/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ranksys.formats.rec;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import org.ranksys.formats.parsing.Parser;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.ranksys.formats.parsing.Parsers.pdp;

/**
 *
 * @author saul
 */
public class TRECRecommendationFormat<U, I> extends TuplesRecommendationFormat<U, I> {

    public TRECRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
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
                }
        );
    }
}
