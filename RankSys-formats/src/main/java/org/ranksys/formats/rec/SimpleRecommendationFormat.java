/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.rec;

import es.uam.eps.ir.ranksys.core.Recommendation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import org.jooq.lambda.Unchecked;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.pdp;

/**
 * Simple format for recommendations: tab-separated user-item-score triplets, grouping first by user (not necessarily in order) and then by decreasing order of score.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class SimpleRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private static final Logger LOG = Logger.getLogger(SimpleRecommendationFormat.class.getName());

    private final Parser<U> uParser;
    private final Parser<I> iParser;

    /**
     * Constructor.
     *
     * @param uParser user type parser
     * @param iParser item type parser
     */
    public SimpleRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this.uParser = uParser;
        this.iParser = iParser;
    }

    @Override
    public Writer<U, I> getWriter(OutputStream out) throws IOException {
        return new SimpleWriter(out);
    }

    private class SimpleWriter implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public SimpleWriter(OutputStream out) throws IOException {
            this.writer = new BufferedWriter(new OutputStreamWriter(out));
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {
            U u = recommendation.getUser();
            recommendation.getItems().forEach(Unchecked.consumer(t -> {
                writer.write(u.toString());
                writer.write("\t");
                writer.write(t.v1.toString());
                writer.write("\t");
                writer.write(Double.toString(t.v2));
                writer.newLine();
            }));
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

    }

    @Override
    public Reader<U, I> getReader(InputStream in) throws IOException {
        return new SimpleReader(in);
    }

    private class SimpleReader implements RecommendationFormat.Reader<U, I> {

        private final InputStream in;

        public SimpleReader(InputStream in) {
            this.in = in;
        }

        @Override
        public Stream<Recommendation<U, I>> readAll() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            RecommendationIterator iterator = new RecommendationIterator(reader);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
        }

    }

    private class RecommendationIterator implements Iterator<Recommendation<U, I>> {

        private U lastU = null;
        private I lastI;
        private Double lastS;
        private final BufferedReader reader;
        private boolean eos = false;

        public RecommendationIterator(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public boolean hasNext() {
            if (eos) {
                return false;
            }
            if (lastU == null) {
                String line = null;
                try {
                    line = reader.readLine();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                if (line == null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    return false;
                } else {
                    CharSequence[] tokens = split(line, '\t', 4);
                    lastU = uParser.parse(tokens[0]);
                    lastI = iParser.parse(tokens[1]);
                    lastS = pdp.applyAsDouble(tokens[2]);
                    return true;
                }
            } else {
                return true;
            }
        }

        @Override
        public Recommendation<U, I> next() {
            String line = null;

            List<Tuple2od<I>> list = new ArrayList<>();

            U nextU = lastU;
            list.add(tuple(lastI, lastS));
            try {
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t', 4);
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[1]);
                    Double s = pdp.applyAsDouble(tokens[2]);
                    if (u.equals(lastU)) {
                        list.add(tuple(i, s));
                    } else {
                        lastU = u;
                        lastI = i;
                        lastS = s;
                        break;
                    }
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            if (line == null) {
                lastU = null;
                eos = true;
            }

            return new Recommendation<>(nextU, list);
        }
    }
}
