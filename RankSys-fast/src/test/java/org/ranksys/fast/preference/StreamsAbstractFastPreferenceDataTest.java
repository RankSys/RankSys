/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit test for StreamsAbstractFastPreferenceData.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class StreamsAbstractFastPreferenceDataTest {

    @Test
    public void simpleTest() {
        StreamsAbstractFastPreferenceData<Integer, Integer> preferences = new MockPreferenceDataTest<>();

        List<Integer> idxs = new ArrayList<>();
        List<Double> vs = new ArrayList<>();
        
        preferences.getUidxIidxs(0).forEachRemaining(idxs::add);
        assertEquals(idxs, Arrays.asList(8, 9, 11, 12, 35, 45));
        
        preferences.getUidxVs(0).forEachRemaining(vs::add);
        assertEquals(vs, Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));

        idxs.clear();
        vs.clear();
        
        preferences.getIidxUidxs(0).forEachRemaining(idxs::add);
        assertEquals(idxs, Arrays.asList(18, 20, 100, 101, 102));
        
        preferences.getIidxVs(0).forEachRemaining(vs::add);
        assertEquals(vs, Arrays.asList(6.0, 5.0, 4.0, 3.0, 2.0));
    }

    private static class MockPreferenceDataTest<U, I> extends StreamsAbstractFastPreferenceData<U, I> {

        public MockPreferenceDataTest() {
            super(null, null);
        }

        @Override
        public int numUsers(int iidx) {
            return 5;
        }

        @Override
        public int numItems(int uidx) {
            return 6;
        }

        @Override
        public IntStream getUidxWithPreferences() {
            throw new UnsupportedOperationException("Not needed here.");
        }

        @Override
        public IntStream getIidxWithPreferences() {
            throw new UnsupportedOperationException("Not needed here.");
        }

        @Override
        public Stream<? extends IdxPref> getUidxPreferences(int uidx) {
            return Arrays.asList(
                        new IdxPref(8, 1.0),
                        new IdxPref(9, 2.0),
                        new IdxPref(11, 3.0),
                        new IdxPref(12, 4.0),
                        new IdxPref(35, 5.0),
                        new IdxPref(45, 6.0)
                ).stream();
        }

        @Override
        public Stream<? extends IdxPref> getIidxPreferences(int iidx) {
            return                 Arrays.asList(
                        new IdxPref(18, 6.0),
                        new IdxPref(20, 5.0),
                        new IdxPref(100, 4.0),
                        new IdxPref(101, 3.0),
                        new IdxPref(102, 2.0)
                ).stream();
        }

        @Override
        public int numPreferences() {
            throw new UnsupportedOperationException("Not needed here.");
        }

    }

}
