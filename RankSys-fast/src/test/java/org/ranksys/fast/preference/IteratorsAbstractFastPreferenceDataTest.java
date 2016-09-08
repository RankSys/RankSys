/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import org.ranksys.core.util.iterators.StreamDoubleIterator;
import org.ranksys.core.util.iterators.StreamIntIterator;

/**
 * Unit test for IteratorAbstractFastPreferenceData
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class IteratorsAbstractFastPreferenceDataTest {

    /**
     * Tests main functionalities of the class.
     */
    @Test
    public void simpleTest() {
        IteratorsAbstractFastPreferenceData<Integer, Integer> preferences = new MockPreferenceDataTest<>();

        Assert.assertArrayEquals(
                new IdxPref[]{
                        new IdxPref(8, 1.0),
                        new IdxPref(9, 2.0),
                        new IdxPref(11, 3.0),
                        new IdxPref(12, 4.0),
                        new IdxPref(35, 5.0),
                        new IdxPref(45, 6.0)
                },
                preferences.getUidxPreferences(0).toArray(n -> new IdxPref[n]));

        Assert.assertArrayEquals(
                new IdxPref[]{
                        new IdxPref(18, 6.0),
                        new IdxPref(20, 5.0),
                        new IdxPref(100, 4.0),
                        new IdxPref(101, 3.0),
                        new IdxPref(102, 2.0)
                },
                preferences.getIidxPreferences(0).toArray(n -> new IdxPref[n]));
    }

    private static class MockPreferenceDataTest<U, I> extends IteratorsAbstractFastPreferenceData<U, I> {

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
        public IntIterator getUidxIidxs(int uidx) {
            return new StreamIntIterator(IntStream.of(8, 9, 11, 12, 35, 45));
        }

        @Override
        public DoubleIterator getUidxVs(int uidx) {
            return new StreamDoubleIterator(DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
        }

        @Override
        public IntIterator getIidxUidxs(int iidx) {
            return new StreamIntIterator(IntStream.of(18, 20, 100, 101, 102));
        }

        @Override
        public DoubleIterator getIidxVs(int iidx) {
            return new StreamDoubleIterator(DoubleStream.of(6.0, 5.0, 4.0, 3.0, 2.0));
        }

        @Override
        public int numPreferences() {
            throw new UnsupportedOperationException("Not needed here.");
        }

    }
}
