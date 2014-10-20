/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.ir.ranksys.core.format;

import es.uam.eps.ir.ranksys.core.IdDoublePair;
import es.uam.eps.ir.ranksys.core.recommenders.Recommendation;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.core.util.parsing.Parsers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author saul
 */
public class SimpleRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Parser<U> uParser;
    private final Parser<I> iParser;
    private final Parser<Double> vParser = Parsers.dp;

    public SimpleRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this.uParser = uParser;
        this.iParser = iParser;
    }

    @Override
    public Writer getWriter(String path) throws IOException {
        return new Writer(path);
    }

    public static class Writer<U, I> implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public Writer(String path) throws IOException {
            this.writer = new BufferedWriter(new FileWriter(path));
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {
            U u = recommendation.getUser();
            for (IdDoublePair<I> pair : recommendation.getItems()) {
                writer.write(u.toString());
                writer.write("\t");
                writer.write(pair.id.toString());
                writer.write("\t");
                writer.write(Double.toString(pair.v));
                writer.newLine();
            }
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

    }

    @Override
    public Reader<U, I> getReader(String path) throws IOException {
        return new Reader<>(uParser, iParser, vParser, path);
    }

    public static class Reader<U, I> implements RecommendationFormat.Reader<U, I> {

        private final Parser<U> uParser;
        private final Parser<I> iParser;
        private final Parser<Double> vParser;
        private final String path;

        public Reader(Parser<U> uParser, Parser<I> iParser, Parser<Double> vParser, String path) {
            this.uParser = uParser;
            this.iParser = iParser;
            this.vParser = vParser;
            this.path = path;
        }

        @Override
        public Stream<Recommendation<U, I>> readAll() throws IOException {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                RecommendationIterator iterator = new RecommendationIterator(reader, uParser, iParser, vParser);
                return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
            } catch (IOException ex) {
                getLogger(SimpleRecommendationFormat.class.getName()).log(Level.SEVERE, null, ex);
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
                    CharSequence[] tokens = split(line, '\t');
                    lastU = uParser.parse(tokens[0]);
                    lastI = iParser.parse(tokens[1]);
                    lastS = vParser.parse(tokens[2]);
                    return true;
                }
            } else {
                return true;
            }
        }

        @Override
        public Recommendation<U, I> next() {
            String line = null;

            List<IdDoublePair<I>> list = new ArrayList<>();

            U nextU = lastU;
            list.add(new IdDoublePair<>(lastI, lastS));
            try {
                while ((line = reader.readLine()) != null) {
                    CharSequence[] tokens = split(line, '\t');
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[1]);
                    Double s = vParser.parse(tokens[2]);
                    if (u.equals(lastU)) {
                        list.add(new IdDoublePair<>(i, s));
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
