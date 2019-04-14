/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.rec;

import org.ranksys.core.Recommendation;
import org.jooq.lambda.Unchecked;
import org.ranksys.core.util.tuples.Tuple2od;
import org.ranksys.formats.parsing.Parser;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.ranksys.core.util.FastStringSplitter.split;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import static org.ranksys.formats.parsing.Parsers.pdp;

/**
 * Reader for Mahout-like recommendations. Path must be a directory.
 *
 * @param <U> user type
 * @param <I> item type
 * 
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class MahoutRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Parser<U> uParser;
    private final Parser<I> iParser;

    /**
     * Constructor.
     *
     * @param uParser user parser
     * @param iParser item parser
     */
    public MahoutRecommendationFormat(Parser<U> uParser, Parser<I> iParser) {
        this.uParser = uParser;
        this.iParser = iParser;
    }

    @Override
    public RecommendationFormat.Writer<U, I> getWriter(String path) throws IOException {
        return new Writer(path);
    }

    @Override
    public RecommendationFormat.Writer<U, I> getWriter(File file) throws IOException {
        return getWriter(file.getPath());
    }

    @Override
    public RecommendationFormat.Writer<U, I> getWriter(OutputStream out) throws IOException {
        throw new UnsupportedOperationException("mahout format needs to know the path, sorry!");
    }

    private class Writer implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public Writer(String path) throws IOException {
            new File(path).mkdir();
            this.writer = new BufferedWriter(new FileWriter(path + "/part-0"));
        }

        @Override
        public synchronized void write(Recommendation<U, I> recommendation) throws IOException {
            writer.write(recommendation.getUser() + "\t");
            writer.write(recommendation.getItems().stream()
                    .map(is -> is.v1 + ":" + is.v2)
                    .collect(joining(",", "[", "]")));
            writer.newLine();
        }

        @Override
        public void close() throws IOException {
            writer.flush();
            writer.close();
        }

    }

    @Override
    public RecommendationFormat.Reader<U, I> getReader(String path) throws IOException {
        return new Reader(path);
    }

    @Override
    public RecommendationFormat.Reader<U, I> getReader(File file) throws IOException {
        return getReader(file.getPath());
    }

    @Override
    public RecommendationFormat.Reader<U, I> getReader(InputStream in) throws IOException {
        throw new UnsupportedOperationException("mahout format needs to know the path, sorry!");
    }

    private class Reader implements RecommendationFormat.Reader<U, I> {

        private final String path;

        public Reader(String path) {
            this.path = path;
        }

        @Override
        public Stream<Recommendation<U, I>> readAll() throws IOException {
            return Stream.of(new File(path).listFiles((dir, name) -> name.startsWith("part-")))
                    .map(Unchecked.function(file -> new BufferedReader(new FileReader(file))))
                    .map(Unchecked.function(this::loadPart))
                    .reduce(Stream.empty(), Stream::concat);
        }
        
        @Override
        public void close() throws IOException
        {
            
        }

        private Stream<Recommendation<U, I>> loadPart(BufferedReader reader) throws IOException {
            return reader.lines().map(line -> {
                CharSequence[] toks1 = split(line, '\t');

                U user = uParser.parse(toks1[0]);

                CharSequence[] toks2 = split(toks1[1].subSequence(1, toks1[1].length() - 1), ',');
                if (toks2[0].length() == 0) {
                    return new Recommendation<U, I>(user, Collections.emptyList());
                }
                List<Tuple2od<I>> items = Stream.of(toks2).map(is -> {
                    CharSequence[] toks3 = split(is, ':');

                    I i = iParser.parse(toks3[0]);
                    double s = pdp.applyAsDouble(toks3[1]);

                    return tuple(i, s);
                }).collect(toList());

                return new Recommendation<U, I>(user, items);
            });
        }
    }
}
