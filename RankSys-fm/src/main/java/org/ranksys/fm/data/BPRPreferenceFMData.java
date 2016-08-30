/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.data;

import org.ranksys.core.preference.fast.FastPreferenceData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Random;
import java.util.stream.Stream;
import static org.jooq.lambda.Seq.seq;
import org.ranksys.javafm.FMInstance;
import org.ranksys.javafm.data.FMData;

/**
 * Samples user preferences for a BPR-like loss minimisation.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class BPRPreferenceFMData implements FMData {

    private static final double[] UIJ_VALUES = {1.0, 2.0, 3.0};

    private final FastPreferenceData<?, ?> prefs;
    private final Random rnd;
    private final IntArrayList uidxs;
    private final IntArrayList iidxs;

    /**
     * Constructor.
     *
     * @param prefs preference data
     */
    public BPRPreferenceFMData(FastPreferenceData<?, ?> prefs) {
        this(prefs, new Random());
    }

    /**
     * Constructor.
     *
     * @param prefs preference data
     * @param rnd random number generator
     */
    public BPRPreferenceFMData(FastPreferenceData<?, ?> prefs, Random rnd) {
        this.prefs = prefs;
        this.rnd = rnd;

        this.uidxs = new IntArrayList();
        prefs.getUidxWithPreferences().forEach(uidxs::add);

        this.iidxs = new IntArrayList();
        prefs.getIidxWithPreferences().forEach(iidxs::add);
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

    private FMInstance getInstance(int uidx, int iidx, int jidx) {
        int nu = prefs.numUsers();
        return new FMInstance(1.0, new int[]{uidx, iidx + nu, jidx + nu}, UIJ_VALUES);
    }

    @Override
    public Stream<? extends FMInstance> stream() {
        return uidxs.stream()
                .flatMap(uidx -> {
                    IntSet uidxIidxs = new IntOpenHashSet();
                    prefs.getUidxIidxs(uidx).forEachRemaining(uidxIidxs::add);

                    return seq(rnd.ints(iidxs.size(), 0, iidxs.size()).map(iidxs::getInt))
                            .filter(jidx -> !uidxIidxs.contains(jidx))
                            .limit(uidxIidxs.size())
                            .zip(uidxIidxs)
                            .map(t -> getInstance(uidx, t.v2, t.v1))
                            .shuffle();
                });
    }

}
