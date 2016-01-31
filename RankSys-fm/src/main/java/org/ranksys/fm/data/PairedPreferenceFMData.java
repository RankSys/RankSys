/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.data;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.fast.preference.FastPointWisePreferenceData;
import org.ranksys.javafm.instance.PairedFMInstance;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class PairedPreferenceFMData extends AbstractPreferenceFMData<PairedFMInstance> {

    private final IntArrayList itemList;

    public PairedPreferenceFMData(FastPointWisePreferenceData<?, ?> prefs, Random rnd) {
        super(prefs, rnd);

        itemList = new IntArrayList();
        prefs.getIidxWithPreferences().forEach(itemList::add);
        IntArrays.shuffle(itemList.elements(), 0, itemList.size(), rnd);
        int l = itemList.size();

    }

    @Override
    public int numInstances() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int numFeatures() {
        return prefs.numUsers() + prefs.numItems();
    }

    @Override
    public Stream<PairedFMInstance> stream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<PairedFMInstance> stream(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<PairedFMInstance> sample(int n) {
        int nu = prefs.numUsers();
        int ni = prefs.numItems();
        
        return Stream.generate(() -> {
            int uidx = sampler.sample();

            int k = rnd.nextInt(prefs.numItems(uidx));
            int iidx = prefs.getUidxPreferences(uidx).skip(k).findFirst().get().idx;

            IntPredicate isPref = jidx -> ((FastPointWisePreferenceData<?, ?>) prefs).getPreference(uidx, jidx).isPresent();
            
            int jidx = IntStream.iterate(rnd.nextInt(ni), i -> (i + 1) % ni)
                    .limit(ni)
                    .map(i -> itemList.getInt(i))
                    .filter(isPref.negate())
                    .findFirst().getAsInt();

            Int2DoubleMap map = new Int2DoubleOpenHashMap();
            map.put(uidx, 1.0);

            return new PairedFMInstance(iidx + nu, 1.0, jidx + nu, 1.0, 1.0, map);
        }).limit(n);
    }

}
