/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.fast;

import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import static java.lang.Math.min;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2od;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Test for AbstractFastRecommender
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class AbstractFastRecommenderTest {

    private final List<Tuple2id> recs;
    private final FastRecommender<String, String> recommender;

    public AbstractFastRecommenderTest() {
        FastUserIndex<String> uIndex = new SimpleFastUserIndex<String>() {
            {
                add("0");
            }
        };
        FastItemIndex<String> iIndex = new SimpleFastItemIndex<String>() {
            {
                add("0");
                add("1");
                add("2");
                add("3");
                add("4");
                add("5");
            }
        };

        recs = Arrays.asList(
                tuple(0, 6.0),
                tuple(1, 5.0),
                tuple(2, 4.0),
                tuple(3, 3.0),
                tuple(4, 2.0),
                tuple(5, 1.0)
        );

        recommender = new AbstractFastRecommender<String, String>(uIndex, iIndex) {
            @Override
            public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
                
                List<Tuple2id> recs0;
                if (uidx == 0) {
                    recs0 = recs.stream()
                            .filter(p -> filter.test(p.v1))
                            .sorted(comparingDouble(Tuple2id::v2).reversed())
                            .limit(min(maxLength, recs.size()))
                            .collect(toList());
                } else {
                    recs0 = emptyList();
                }

                return new FastRecommendation(uidx, recs0);
            }
        };
    }

    @Test
    public void testSubclass() {
        int maxLength = Integer.MAX_VALUE;
        IntPredicate filter = i -> true;
        
        List<Tuple2id> result0 = recommender.getRecommendation(0, maxLength, filter).getIidxs();

        assertEquals(recs, result0);

        List<Tuple2id> result1 = recommender.getRecommendation(1, maxLength, filter).getIidxs();

        assertEquals(emptyList(), result1);
    }

    @Test
    public void testFilter() {
        int maxLength = Integer.MAX_VALUE;
        IntPredicate filter = i -> i % 2 == 0;
        
        List<Tuple2id> result = recommender.getRecommendation(0, maxLength, filter).getIidxs();

        List<Tuple2id> expected = Arrays.asList(
                tuple(0, 6.0),
                tuple(2, 4.0),
                tuple(4, 2.0)
        );
        
        assertEquals(expected, result);
    }

    @Test
    public void testMaxLength() {
        int maxLength = 2;
        IntPredicate filter = i -> true;
        
        List<Tuple2id> result = recommender.getRecommendation(0, maxLength, filter).getIidxs();

        List<Tuple2id> expected = Arrays.asList(
                tuple(0, 6.0),
                tuple(1, 5.0)
        );
        
        assertEquals(expected, result);
    }
    
    @Test
    public void testFastFree() {
        int maxLength = Integer.MAX_VALUE;
        
        List<Tuple2id> result = recommender.getRecommendation(0, maxLength).getIidxs();

        List<Tuple2id> expected = Arrays.asList(
                tuple(0, 6.0),
                tuple(1, 5.0),
                tuple(2, 4.0),
                tuple(3, 3.0),
                tuple(4, 2.0),
                tuple(5, 1.0)
        );
        
        assertEquals(expected, result);
    }

    @Test
    public void testFastCandidates() {
        IntStream candidates = IntStream.of(1, 3, 5);
        
        List<Tuple2id> result = recommender.getRecommendation(0, candidates).getIidxs();

        List<Tuple2id> expected = Arrays.asList(
                tuple(1, 5.0),
                tuple(3, 3.0),
                tuple(5, 1.0)
        );
        
        assertEquals(expected, result);
    }

    @Test
    public void testStdFilter() {
        int maxLength = Integer.MAX_VALUE;
        Predicate<String> filter = item -> true;
        
        List<Tuple2od<String>> result = recommender.getRecommendation("0", maxLength, filter).getItems();

        List<Tuple2od<String>> expected = Arrays.asList(
                tuple("0", 6.0),
                tuple("1", 5.0),
                tuple("2", 4.0),
                tuple("3", 3.0),
                tuple("4", 2.0),
                tuple("5", 1.0)
        );
        
        assertEquals(expected, result);
    }

    @Test
    public void testStdFree() {
        int maxLength = Integer.MAX_VALUE;
        
        List<Tuple2od<String>> result = recommender.getRecommendation("0", maxLength).getItems();

        List<Tuple2od<String>> expected = Arrays.asList(
                tuple("0", 6.0),
                tuple("1", 5.0),
                tuple("2", 4.0),
                tuple("3", 3.0),
                tuple("4", 2.0),
                tuple("5", 1.0)
        );
        
        assertEquals(expected, result);
    }

    @Test
    public void testStdCandidates() {
        Stream<String> candidates = Stream.of("1", "3", "5");
        
        List<Tuple2od<String>> result = recommender.getRecommendation("0", candidates).getItems();

        List<Tuple2od<String>> expected = Arrays.asList(
                tuple("1", 5.0),
                tuple("3", 3.0),
                tuple("5", 1.0)
        );
        
        assertEquals(expected, result);
    }

}
