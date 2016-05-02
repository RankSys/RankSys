/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.index;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.IntStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit test for SimpleFastFeatureIndex
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class SimpleFastFeatureIndexTest {

    @Test
    public void test() {
        Random rnd = new Random();
        
        int N = rnd.nextInt(5000);
        List<String> features = IntStream.range(0, N).mapToObj(Integer::toString).collect(toList());
        Collections.shuffle(features, rnd);

        FastFeatureIndex<String> featureIndex = SimpleFastFeatureIndex.load(features.stream());

        assertEquals(featureIndex.numFeatures(), N);

        rnd.ints(1000, 0, N).forEach(fidx -> {
            assertTrue(featureIndex.fidx2feature(fidx).equals(features.get(fidx)));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(feature -> {
            assertTrue(featureIndex.feature2fidx(feature) == features.indexOf(feature));
        });

        rnd.ints(1000, 0, N).mapToObj(Integer::toString).forEach(feature -> {
            assertTrue(featureIndex.containsFeature(feature));
        });

        rnd.ints(1000, N, 2 * N).mapToObj(Integer::toString).forEach(feature -> {
            assertFalse(featureIndex.containsFeature(feature));
        });

        assertEquals(featureIndex.getAllFeatures().collect(toSet()), new HashSet<>(features));
    }

}
