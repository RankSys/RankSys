/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.data;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import java.util.Random;
import org.ranksys.core.util.sampling.WeightedSampling;
import org.ranksys.javafm.data.FMData;
import org.ranksys.javafm.instance.FMInstance;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public abstract class AbstractPreferenceFMData<I extends FMInstance> implements FMData<I> {

    protected final FastPreferenceData<?, ?> prefs;
    protected final Random rnd;
    protected final WeightedSampling<Integer> sampler;

    public AbstractPreferenceFMData(FastPreferenceData<?, ?> prefs, Random rnd) {
        this.prefs = prefs;
        this.rnd = rnd;
        this.sampler = new WeightedSampling<>(prefs.getUidxWithPreferences().boxed(), uidx -> 1.0, true, rnd);
    }

    @Override
    public int numFeatures() {
        return prefs.numUsers() + prefs.numItems();
    }

}
