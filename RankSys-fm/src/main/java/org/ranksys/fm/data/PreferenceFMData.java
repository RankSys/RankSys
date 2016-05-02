/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.data;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap.BasicEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.ranksys.javafm.FMInstance;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.data.ListWiseFMData;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class PreferenceFMData implements FMData, ListWiseFMData {

    protected final FastPreferenceData<?, ?> prefs;
    protected final Random rnd;
    protected final IntArrayList uidxs;

    public PreferenceFMData(FastPreferenceData<?, ?> prefs, Random rnd) {
        this.prefs = prefs;
        this.rnd = rnd;

        this.uidxs = new IntArrayList();
        prefs.getUidxWithPreferences().forEach(uidxs::add);
    }

    @Override
    public int numFeatures() {
        return prefs.numUsers() + prefs.numItems();
    }

    @Override
    public int numInstances() {
        return prefs.numPreferences();
    }

    @Override
    public void shuffle() {
        IntArrays.shuffle(uidxs.elements(), 0, uidxs.size(), rnd);
    }

    protected FMInstance getInstance(int uidx, IdxPref p) {
        int[] k = {uidx, p.v1 + prefs.numUsers()};
        double[] v = {1.0, 1.0};
        
        return new FMInstance(p.v2, k, v);
    }

    @Override
    public Stream<? extends FMInstance> stream() {
        return uidxs.stream()
                .flatMap(uidx -> prefs.getUidxPreferences(uidx)
                        .map(p -> getInstance(uidx, p)));
    }

    @Override
    public Stream<Entry<List<? extends FMInstance>>> streamByGroup() {
        return uidxs.stream()
                .map(uidx -> {
                    List<FMInstance> instances = prefs.getUidxPreferences(uidx)
                            .map(p -> getInstance(uidx, p))
                            .collect(toList());

                    return new BasicEntry<>(uidx, instances);
                });
    }

}
