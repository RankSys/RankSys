/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ranksys.formats.rec;

import es.uam.eps.ir.ranksys.core.IdDouble;
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
import static java.util.logging.Logger.getLogger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.dp;

/**
 *
 * @author saul
 */
public class TRECRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Parser<U> uParser;
    private final Parser<I> iParser;
    private final Parser<Double> vParser = dp;

    public TRECRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this.uParser = uParser;
        this.iParser = iParser;
    }

    @Override
    public Writer<U, I> getWriter(OutputStream out) throws IOException {
        return new Writer<>(out);
    }

    public static class Writer<U, I> implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public Writer(OutputStream out) throws IOException {
            this.writer = new BufferedWriter(new OutputStreamWriter(out));
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {
            int n = 1;
            U u = recommendation.getUser();
            for (IdDouble<I> pair : recommendation.getItems()) {
                writer.write(u.toString());
                writer.write("\tQ0\t");
                writer.write(pair.id.toString());
                writer.write("\t");
                writer.write(Integer.toString(n));
                writer.write("\t");
                writer.write(Double.toString(pair.v));
                writer.write("\tr");
                writer.newLine();
                n++;
            }
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

    }

    @Override
    public Reader<U, I> getReader(InputStream in) throws IOException {
        return new Reader<>(uParser, iParser, vParser, in);
    }

    public static class Reader<U, I> implements RecommendationFormat.Reader<U, I> {

        private final Parser<U> uParser;
        private final Parser<I> iParser;
        private final Parser<Double> vParser;
        private final InputStream in;

        public Reader(Parser<U> uParser, Parser<I> iParser, Parser<Double> vParser, InputStream in) {
            this.uParser = uParser;
            this.iParser = iParser;
            this.vParser = vParser;
            this.in = in;
        }

        @Override
        public Stream<Recommendation<U, I>> readAll() throws IOException {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                RecommendationIterator<U, I> iterator = new RecommendationIterator<>(reader, uParser, iParser, vParser);
                return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
            } catch (IOException ex) {
                getLogger(TRECRecommendationFormat.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

    }

    private static class RecommendationIterator<U, I> implements Iterator<Recommendation<U, I>> {

        private U lastU = null;
        private I lastI;
        private Double lastS;
        private final BufferedReader reader;
        private final Parser<U> uParser;
        private final Parser<I> iParser;
        private final Parser<Double> vParser;
        private boolean eos = false;

        public RecommendationIterator(BufferedReader reader, final Parser<U> uParser, final Parser<I> iParser, final Parser<Double> vParser) throws IOException {
            this.reader = reader;
            this.uParser = uParser;
            this.iParser = iParser;
            this.vParser = vParser;
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
                    getLogger(Recommendation.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (line == null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        getLogger(Recommendation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return false;
                } else {
                    CharSequence[] tokens = split(line, '\t', 6);
                    lastU = uParser.parse(tokens[0]);
                    lastI = iParser.parse(tokens[2]);
                    lastS = vParser.parse(tokens[4]);
                    return true;
                }
            } else {
                return true;
            }
        }

        @Override
        public Recommendation<U, I> next() {
            String line = null;

            List<IdDouble<I>> list = new ArrayList<>();

            U nextU = lastU;
            list.add(new IdDouble<>(lastI, lastS));
            try {
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t', 6);
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[2]);
                    Double s = vParser.parse(tokens[4]);
                    if (u.equals(lastU)) {
                        list.add(new IdDouble<>(i, s));
                    } else {
                        lastU = u;
                        lastI = i;
                        lastS = s;
                        break;
                    }
                }
            } catch (IOException ex) {
                getLogger(Recommendation.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (line == null) {
                lastU = null;
                eos = true;
            }

            return new Recommendation<>(nextU, list);
        }
    }
}
