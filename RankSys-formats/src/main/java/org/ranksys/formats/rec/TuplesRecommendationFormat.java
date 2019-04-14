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
import org.jooq.lambda.function.Function4;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.core.util.tuples.Tuple2od;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static org.jooq.lambda.Seq.seq;

/**
 * Reader for formats where recommendations are stored in tuples, one each line.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <U> user type
 * @param <I> item type
 */
public class TuplesRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Function4<U, I, Double, Long, String> tupleWriter;
    private final Function<String, Tuple3<U, I, Double>> tupleReader;
    private final boolean sortByDecreasingScore;

    /**
     * Constructor.
     *
     * @param tupleWriter tuple writer
     * @param tupleReader tuple reader
     */
    public TuplesRecommendationFormat(Function4<U, I, Double, Long, String> tupleWriter, Function<String, Tuple3<U, I, Double>> tupleReader) {
        this(tupleWriter, tupleReader, false);
    }

    /**
     * Constructor.
     *
     * @param tupleWriter tuple writer
     * @param tupleReader tuple reader
     * @param sortByDecreasingScore sort read tuples by decreasing score?
     */
    public TuplesRecommendationFormat(Function4<U, I, Double, Long, String> tupleWriter, Function<String, Tuple3<U, I, Double>> tupleReader, boolean sortByDecreasingScore) {
        this.tupleWriter = tupleWriter;
        this.tupleReader = tupleReader;
        this.sortByDecreasingScore = sortByDecreasingScore;
    }

    @Override
    public RecommendationFormat.Writer<U, I> getWriter(OutputStream out) throws IOException {
        return new Writer(out);
    }

    /**
     * Writer of recommendations.
     */
    protected class Writer implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        /**
         * Constructor.
         *
         * @param out output stream where recommendations are written
         * @throws IOException when I/O problems
         */
        public Writer(OutputStream out) throws IOException {
            this.writer = new BufferedWriter(new OutputStreamWriter(out));
        }

        @Override
        public synchronized void write(Recommendation<U, I> recommendation) throws IOException {
            U u = recommendation.getUser();
            seq(recommendation.getItems())
                    .zipWithIndex()
                    .map(t -> tupleWriter.apply(u, t.v1.v1, t.v1.v2, t.v2))
                    .forEach(Unchecked.consumer(line -> {
                        writer.write(line);
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

    /**
     * Reader of recommendations.
     */
    protected class Reader implements RecommendationFormat.Reader<U, I> {

        private final InputStream in;

        /**
         * Constructor.
         *
         * @param in input stream where to read recommendation
         */
        public Reader(InputStream in) {
            this.in = in;
        }

        @Override
        public Stream<Recommendation<U, I>> readAll() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in), 128 * 1024);

            return groupAdjacent(reader.lines().map(tupleReader), (t1, t2) -> t1.v1.equals(t2.v1))
                    .map(userTuples -> {
                        U user = userTuples.get(0).v1;

                        List<Tuple2od<I>> items = userTuples.stream()
                                .map(Tuple3::skip1)
                                .map(Tuple2od::new)
                                .collect(toList());

                        if (sortByDecreasingScore) {
                            items.sort(Comparator.comparingDouble((Tuple2od<I> r) -> r.v2)
                                    .reversed());
                        }

                        return new Recommendation<>(user, items);
                    });
        }
        
        @Override
        public void close() throws IOException
        {
            this.in.close();
        }

    }

    private static <T> Stream<List<T>> groupAdjacent(Stream<T> tuples, BiPredicate<T, T> adjacent) {
        return StreamSupport.stream(spliteratorUnknownSize(new Iterator<List<T>>() {

            Iterator<T> it = tuples.iterator();
            List<T> nextList = new ArrayList<>();

            @Override
            public boolean hasNext() {
                return !nextList.isEmpty() || it.hasNext();
            }

            @Override
            public List<T> next() {
                List<T> list = nextList;
                nextList = new ArrayList<>();

                if (it.hasNext()) {
                    T t = it.next();
                    boolean adj = list.isEmpty() || adjacent.test(t, list.get(0));
                    while (adj && it.hasNext()) {
                        list.add(t);
                        t = it.next();
                        adj = adjacent.test(t, list.get(0));
                    }

                    if (adj) {
                        list.add(t);
                        return list;
                    } else {
                        nextList.add(t);
                        return list;
                    }
                } else {
                    return list;
                }
            }
        }, ORDERED), false);
    }

}
