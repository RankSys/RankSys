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
import static java.util.Spliterator.ORDERED;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.function.Function;
import org.jooq.lambda.tuple.Tuple3;
import static java.util.stream.Collectors.toList;
import org.ranksys.core.util.tuples.Tuple2od;
import static java.util.Spliterators.spliteratorUnknownSize;
import static org.jooq.lambda.Seq.seq;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.function.Function4;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class TuplesRecommendationFormat<U, I> implements RecommendationFormat<U, I> {

    private final Function4<U, I, Double, Long, String> tupleWriter;
    private final Function<String, Tuple3<U, I, Double>> tupleReader;

    public TuplesRecommendationFormat(Function4<U, I, Double, Long, String> tupleWriter, Function<String, Tuple3<U, I, Double>> tupleReader) {
        this.tupleWriter = tupleWriter;
        this.tupleReader = tupleReader;
    }

    @Override
    public RecommendationFormat.Writer<U, I> getWriter(OutputStream out) throws IOException {
        return new Writer(out);
    }

    protected class Writer implements RecommendationFormat.Writer<U, I> {

        private final BufferedWriter writer;

        public Writer(OutputStream out) throws IOException {
            this.writer = new BufferedWriter(new OutputStreamWriter(out));
        }

        @Override
        public void write(Recommendation<U, I> recommendation) throws IOException {
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

    protected class Reader implements RecommendationFormat.Reader<U, I> {

        private final InputStream in;

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

                        return new Recommendation<>(user, items);
                    });
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
