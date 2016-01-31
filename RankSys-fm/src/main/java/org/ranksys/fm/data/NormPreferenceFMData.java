/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.data;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.Random;
import java.util.stream.Stream;
import org.ranksys.fast.preference.FastPointWisePreferenceData;
import org.ranksys.javafm.instance.NormFMInstance;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class NormPreferenceFMData extends AbstractPreferenceFMData<NormFMInstance> {

    public NormPreferenceFMData(FastPointWisePreferenceData<?, ?> prefs, Random rnd) {
        super(prefs, rnd);
    }

    @Override
    public int numInstances() {
        return prefs.numPreferences();
    }

    @Override
    public Stream<NormFMInstance> stream() {
        return prefs.getUidxWithPreferences().mapToObj(uidx -> uidx)
                .flatMap(uidx -> {
                    return prefs.getUidxPreferences(uidx)
                            .map(p -> {
                                Int2DoubleMap map = new Int2DoubleOpenHashMap();
                                map.put(uidx.intValue(), 1.0);
                                map.put(p.idx + prefs.numUsers(), 1.0);

                                return new NormFMInstance(uidx, p.v, map);
                            });
                });
    }

    @Override
    public Stream<NormFMInstance> stream(int i) {
        if (i < prefs.numUsers()) {
            return prefs.getUidxPreferences(i)
                    .map(p -> {
                        Int2DoubleMap map = new Int2DoubleOpenHashMap();
                        map.put(i, 1.0);
                        map.put(p.idx + prefs.numUsers(), 1.0);

                        return new NormFMInstance(i, p.v, map);
                    });
        } else {
            return prefs.getIidxPreferences(i - prefs.numUsers())
                    .map(p -> {
                        Int2DoubleMap map = new Int2DoubleOpenHashMap();
                        map.put(p.idx, 1.0);
                        map.put(i, 1.0);

                        return new NormFMInstance(p.idx, p.v, map);
                    });
        }
    }

    @Override
    public Stream<NormFMInstance> sample(int n) {
        return Stream.generate(() -> {
            int uidx = sampler.sample();

            int k = rnd.nextInt(prefs.numItems(uidx));
            IdxPref p = prefs.getUidxPreferences(uidx).skip(k).findFirst().get();

            Int2DoubleMap map = new Int2DoubleOpenHashMap();
            map.put(uidx, 1.0);
            map.put(p.idx + prefs.numUsers(), 1.0);

            return new NormFMInstance(uidx, p.v, map);
        }).limit(n);
    }

}
