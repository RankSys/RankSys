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
import java.io.FileNotFoundException;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.pdp;

/**
 *
 * @author saul
 */
public class ZipRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Parser<U> uParser;
    private final Parser<I> iParser;
    private final boolean ignoreScores;

    public ZipRecommendationFormat(Parser<U> uParser, Parser<I> iParser, boolean ignoreScores) {
        this.uParser = uParser;
        this.iParser = iParser;
        this.ignoreScores = ignoreScores;
    }

    @Override
    public Writer<U, I> getWriter(OutputStream out) throws IOException {
        return new Writer<>(out, ignoreScores);
    }

    public static class Writer<U, I> implements RecommendationFormat.Writer<U, I> {

        private final ZipOutputStream zip;
        private final BufferedWriter writer;
        private final boolean ignoreScores;

        public Writer(OutputStream out, boolean ignoreScores) throws FileNotFoundException, IOException {
            this.zip = new ZipOutputStream(out);
            zip.putNextEntry(new ZipEntry("recommendation"));
            writer = new BufferedWriter(new OutputStreamWriter(zip));
            this.ignoreScores = ignoreScores;
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {

            U u = recommendation.getUser();
            for (Tuple2od<I> pair : recommendation.getItems()) {
                writer.write(u.toString());
                writer.write("\t");
                writer.write(pair.v1.toString());
                if (!ignoreScores) {
                    writer.write("\t");
                    writer.write(Double.toString(pair.v2));
                }
                writer.newLine();
            }
        }

        @Override
        public void close() throws IOException {
            writer.flush();
            zip.closeEntry();
            zip.close();
        }

    }

    @Override
    public Reader<U, I> getReader(InputStream in) throws IOException {
        return new Reader<>(uParser, iParser, ignoreScores, in);
    }

    public static class Reader<U, I> implements RecommendationFormat.Reader<U, I> {

        private final Parser<U> uParser;
        private final Parser<I> iParser;
        private final boolean ignoreScores;
        private final InputStream in;

        public Reader(Parser<U> uParser, Parser<I> iParser, boolean ignoreScores, InputStream in) {
            this.uParser = uParser;
            this.iParser = iParser;
            this.ignoreScores = ignoreScores;
            this.in = in;
        }

        @Override
        public Stream<Recommendation<U, I>> readAll() throws IOException {
            try {
                ZipInputStream zip = new ZipInputStream(in);
                zip.getNextEntry();
                BufferedReader reader = new BufferedReader(new InputStreamReader(zip), 128 * 1024);

                RecommendationIterator<U, I> iterator = new RecommendationIterator<>(reader, uParser, iParser, ignoreScores);
                return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
            } catch (IOException ex) {
                getLogger(ZipRecommendationFormat.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

    }

    private static class RecommendationIterator<U, I> implements Iterator<Recommendation<U, I>> {

        private U lastU = null;
        private I lastI;
        private double lastS;
        private final BufferedReader reader;
        private final Parser<U> uParser;
        private final Parser<I> iParser;
        private final boolean ignoreScores;
        private boolean eos = false;

        public RecommendationIterator(BufferedReader reader, final Parser<U> uParser, final Parser<I> iParser, final boolean ignoreScores) throws IOException {
            this.reader = reader;
            this.uParser = uParser;
            this.iParser = iParser;
            this.ignoreScores = ignoreScores;
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
                    CharSequence[] tokens = split(line, '\t', ignoreScores ? 3 : 4);
                    lastU = uParser.parse(tokens[0]);
                    lastI = iParser.parse(tokens[1]);
                    lastS = ignoreScores ? 1.0 : pdp.applyAsDouble(tokens[2]);
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
                    CharSequence[] tokens = split(line, '\t', ignoreScores ? 3 : 4);
                    U u = uParser.parse(tokens[0]);
                    I i = iParser.parse(tokens[1]);
                    double s = ignoreScores ? 1.0 : pdp.applyAsDouble(tokens[2]);
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
