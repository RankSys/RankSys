/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.binom;

import org.ranksys.novdiv.binom.BinomialModel;
import org.ranksys.core.feature.FeatureData;
import org.ranksys.core.feature.SimpleFeatureData;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.stream.Stream;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.ranksys.core.preference.PreferenceData;
import org.ranksys.core.preference.SimplePreferenceData;

/**
 * Unit test for BinomialModel.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class BinomialModelTest {

    private FeatureData<Integer, Integer, Double> featureData;
    private PreferenceData<Integer, Integer> preferences;

    /**
     * Loads some mock data for test.
     */
    @Before
    public void loadData() {
        featureData = SimpleFeatureData.load(Stream.of(
                tuple(1, 1, 1.0),
                tuple(2, 1, 1.0),
                tuple(3, 2, 1.0)
        ));

        preferences = SimplePreferenceData.load(Stream.of(
                tuple(1, 1, 1.0),
                tuple(1, 2, 1.0),
                tuple(2, 1, 1.0),
                tuple(2, 2, 1.0),
                tuple(2, 3, 1.0)
        ));
    }
    
    /**
     * Tests the global probability method (BinomialModel::p).
     */
    @Test
    public void testGlobalModel() {
        Int2DoubleMap expectedGlobalP = new Int2DoubleOpenHashMap();
        expectedGlobalP.put(1, 0.8);
        expectedGlobalP.put(2, 0.2);
        
        BinomialModel<Integer, Integer, Integer> bm = new BinomialModel<>(false, Stream.empty(), preferences, featureData, 0.0);
        
        assertEquals(expectedGlobalP.keySet(), bm.getFeatures());
        expectedGlobalP.forEach((f, p) -> assertEquals((double) p, bm.p(f), 0.0001));
    }
    
    /**
     * Tests the local (user) probability method with alpha = 0.0.
     */
    @Test
    public void testLocalModelAlpha00() {
        Int2DoubleMap expectedLocalP = new Int2DoubleOpenHashMap();
        expectedLocalP.put(1, 0.8);
        expectedLocalP.put(2, 0.2);
        
        checkLocalModel(0.0, expectedLocalP);
    }
    
    /**
     * Tests the local (user) probability method with alpha = 0.5.
     */
    @Test
    public void testLocalModelAlpha05() {
        Int2DoubleMap expectedLocalP = new Int2DoubleOpenHashMap();
        expectedLocalP.put(1, 0.9);
        expectedLocalP.put(2, 0.1);
        
        checkLocalModel(0.5, expectedLocalP);
    }
    
    /**
     * Tests the local (user) probability method with alpha = 1.0.
     */
    @Test
    public void testLocalModelAlpha10() {
        Int2DoubleMap expectedLocalP = new Int2DoubleOpenHashMap();
        expectedLocalP.put(1, 1.0);
        
        checkLocalModel(1.0, expectedLocalP);
    }
    
    private void checkLocalModel(double alpha, Int2DoubleMap expectedLocalP) {
        BinomialModel<Integer, Integer, Integer> bm = new BinomialModel<>(false, Stream.empty(), preferences, featureData, alpha);
        BinomialModel<Integer, Integer, Integer>.UserBinomialModel ubm = bm.getModel(1);
        
        assertEquals(expectedLocalP.keySet(), ubm.getFeatures());
        expectedLocalP.forEach((f, p) -> assertEquals((double) p, ubm.p(f), 0.0001));
    }

}
