/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ranksys.formats.rec;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
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
 *
 * @author saul
 */
public class ZipRecommendationFormat<U, I> extends TuplesRecommendationFormat<U, I> {

    public ZipRecommendationFormat(Parser<U> uParser, Parser<I> iParser, boolean ignoreScores) {
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
                }
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
