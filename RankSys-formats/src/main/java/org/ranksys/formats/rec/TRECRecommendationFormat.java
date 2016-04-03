/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ranksys.formats.rec;

import es.uam.eps.ir.ranksys.core.Recommendation;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
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
import static org.jooq.lambda.Seq.seq;
import org.jooq.lambda.Unchecked;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.pdp;

/**
 *
 * @author saul
 */
public class TRECRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private static final Logger LOG = Logger.getLogger(TRECRecommendationFormat.class.getName());

    private final Parser<U> uParser;
    private final Parser<I> iParser;

    public TRECRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this.uParser = uParser;
        this.iParser = iParser;
    }

    @Override
    public RecommendationFormat.Writer<U, I> getWriter(OutputStream out) throws IOException {
        return new Writer(out);
    }

    private class Writer implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public Writer(OutputStream out) throws IOException {
            this.writer = new BufferedWriter(new OutputStreamWriter(out));
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {
            U u = recommendation.getUser();
            seq(recommendation.getItems()).zipWithIndex().forEach(Unchecked.consumer(t -> {
                writer.write(u.toString());
                writer.write("\tQ0\t");
                writer.write(t.v1.v1.toString());
                writer.write("\t");
                writer.write(Long.toString(t.v2));
                writer.write("\t");
                writer.write(Double.toString(t.v1.v2));
                writer.write("\tr");
                writer.newLine();
            }));
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

    }

    @Override
    public RecommendationFormat.Reader<U, I> getReader(InputStream in) throws IOException {
        return new Reader(in);
    }

    private class Reader implements RecommendationFormat.Reader<U, I> {

        private final InputStream in;

        public Reader(InputStream in) {
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

        public RecommendationIterator(BufferedReader reader) throws IOException {
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
                    CharSequence[] tokens = split(line, '\t', 6);
                    lastU = uParser.parse(tokens[0]);
                    lastI = iParser.parse(tokens[2]);
                    lastS = pdp.applyAsDouble(tokens[4]);
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
            list.add(new Tuple2od<>(lastI, lastS));
            try {
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t', 6);
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[2]);
                    Double s = pdp.applyAsDouble(tokens[4]);
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
