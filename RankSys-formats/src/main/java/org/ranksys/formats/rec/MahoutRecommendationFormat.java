/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ranksys.formats.rec;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.core.util.FastStringSplitter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.formats.parsing.Parser;
import static org.ranksys.formats.parsing.Parsers.dp;

/**
 *
 * @author saul
 */
public class MahoutRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Parser<U> uParser;
    private final Parser<I> iParser;
    private final Parser<Double> vParser = dp;

    public MahoutRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this.uParser = uParser;
        this.iParser = iParser;
    }

    @Override
    public Writer<U, I> getWriter(String path) throws IOException {
        return new Writer<>(path);
    }

    @Override
    public Writer<U, I> getWriter(File file) throws IOException {
        return getWriter(file.getPath());
    }

    @Override
    public Writer<U, I> getWriter(OutputStream out) throws IOException {
        throw new UnsupportedOperationException("mahout format needs to know the path, sorry!");
    }

    public static class Writer<U, I> implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public Writer(String path) throws IOException {
            File file = new File(path);
            file.mkdir();
            this.writer = new BufferedWriter(new FileWriter(path + "/part-0"));
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {
            writer.write(recommendation.getUser() + "\t");
            writer.write(recommendation.getItems().stream().map(is -> is.v1 + ":" + is.v2).collect(joining(",", "[", "]")));
            writer.newLine();
        }

        @Override
        public void close() throws IOException {
            writer.flush();
            writer.close();
        }

    }

    @Override
    public Reader<U, I> getReader(String path) throws IOException {
        return new Reader<>(uParser, iParser, vParser, path);
    }

    @Override
    public Reader<U, I> getReader(File file) throws IOException {
        return getReader(file.getPath());
    }

    @Override
    public Reader<U, I> getReader(InputStream in) throws IOException {
        throw new UnsupportedOperationException("mahout format needs to know the path, sorry!");
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
            File[] files = new File(path).listFiles((dir, name) -> name.startsWith("part-"));
            return Stream.of(files)
                    .map(file -> {
                        try {
                            return loadPart(new BufferedReader(new FileReader(file)));
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    })
                    .reduce(Stream.empty(), Stream::concat);
        }

        private Stream<Recommendation<U, I>> loadPart(BufferedReader reader) throws IOException {
            return reader.lines().map(line -> {
                CharSequence[] toks1 = FastStringSplitter.split(line, '\t');

                U user = uParser.parse(toks1[0]);

                CharSequence[] toks2 = FastStringSplitter.split(toks1[1].subSequence(1, toks1[1].length() - 1), ',');
                List<Tuple2od<I>> items = Stream.of(toks2).map(is -> {
                    CharSequence[] toks3 = FastStringSplitter.split(is, ':');

                    I i = iParser.parse(toks3[0]);
                    double s = vParser.parse(toks3[1]);

                    return tuple(i, s);
                }).collect(Collectors.toList());

                return new Recommendation<U, I>(user, items);
            });
        }
    }
}
