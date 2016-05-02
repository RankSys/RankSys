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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Random;
import java.util.function.IntPredicate;
import org.ranksys.fast.preference.FastPointWisePreferenceData;
import org.ranksys.javafm.FMInstance;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class BPRPreferenceFMData extends PreferenceFMData {

    private final IntArrayList iidxs;

    public BPRPreferenceFMData(FastPreferenceData<?, ?> prefs) {
        this(prefs, new Random());
    }
    
    public BPRPreferenceFMData(FastPreferenceData<?, ?> prefs, Random rnd) {
        super(prefs, rnd);

        this.iidxs = new IntArrayList();
        prefs.getIidxWithPreferences().forEach(iidxs::add);
    }

    @Override
    protected FMInstance getInstance(int uidx, IdxPref p) {
        IntPredicate isPref = jidx -> ((FastPointWisePreferenceData<?, ?>) prefs).getPreference(uidx, jidx).isPresent();

        int jidx = rnd.ints(iidxs.size(), 0, iidxs.size())
                .map(iidxs::getInt)
                .filter(isPref.negate())
                .findFirst().getAsInt();

        int[] k = {uidx, p.v1 + prefs.numUsers(), jidx + prefs.numUsers()};
        double[] v = {1.0, 2.0, 3.0};
        
        return new FMInstance(1.0, k, v);
    }

}
