/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.mf.rec;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.mf.Factorization;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class MFRecommenderTest {

    private final MFRecommender<String, String> recommender;

    public MFRecommenderTest() {
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

        int K = 2;
        DenseDoubleMatrix2D p = new DenseDoubleMatrix2D(new double[][]{
            new double[]{0.1, 1.0}
        });
        DenseDoubleMatrix2D q = new DenseDoubleMatrix2D(new double[][]{
            new double[]{1.0, 6.0},
            new double[]{1.0, 5.0},
            new double[]{1.0, 4.0},
            new double[]{1.0, 3.0},
            new double[]{1.0, 2.0},
            new double[]{1.0, 1.0}
        });
        Factorization<String, String> factorization = new Factorization<String, String>(uIndex, iIndex, p, q, K) {
        };

        recommender = new MFRecommender<>(uIndex, iIndex, factorization);
    }

    @Test
    public void testFilter() {
        int maxLength = 2;
        IntPredicate filter = i -> i % 2 == 0;

        List<IdxDouble> result = recommender.getRecommendation(0, maxLength, filter).getIidxs();

        List<IdxDouble> expected = Arrays.asList(
                new IdxDouble(0, 6.1),
                new IdxDouble(2, 4.1)
        );
        
        assertEquals(expected, result);
    }

    @Test
    public void testCandidates() {
        IntStream candidates = IntStream.of(1, 3, 5);
        
        List<IdxDouble> result = recommender.getRecommendation(0, candidates).getIidxs();

        List<IdxDouble> expected = Arrays.asList(
                new IdxDouble(1, 5.1),
                new IdxDouble(3, 3.1),
                new IdxDouble(5, 1.1)
        );
        
        assertEquals(expected, result);
    }
}
