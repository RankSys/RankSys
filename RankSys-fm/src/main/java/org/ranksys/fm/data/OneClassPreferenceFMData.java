/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.fm.data;

import org.ranksys.fast.preference.FastPreferenceData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.ranksys.javafm.FMInstance;
import org.ranksys.javafm.data.FMData;

/**
 * Samples user preferences with a number of negative preferences for one class prediction
 * for collaborative filtering.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class OneClassPreferenceFMData implements FMData {

    private static final double[] UI_VALUES = {1.0, 1.0};

    private final FastPreferenceData<?, ?> prefs;
    private final Random rnd;
    private final IntArrayList uidxs;
    private final IntArrayList iidxs;
    private final double negativeProp;

    /**
     * Constructor.
     *
     * @param prefs preference data
     * @param negativeProp proportion of negative instances wrt positive preferences by user
     */
    public OneClassPreferenceFMData(FastPreferenceData<?, ?> prefs, double negativeProp) {
        this(prefs, negativeProp, new Random());
    }
    
    /**
     * Constructor.
     *
     * @param prefs preference data
     * @param negativeProp proportion of negative instances wrt positive preferences by user
     * @param rnd random number generator
     */
    public OneClassPreferenceFMData(FastPreferenceData<?, ?> prefs, double negativeProp, Random rnd) {
        this.prefs = prefs;
        this.negativeProp = negativeProp;
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

    private FMInstance getInstance(int uidx, int iidx, double v) {
        return new FMInstance(v, new int[]{uidx, iidx + prefs.numUsers()}, UI_VALUES);
    }

    @Override
    public Stream<? extends FMInstance> stream() {
        return uidxs.stream()
                .flatMap(uidx -> {
                    IntSet uidxIidxs = new IntOpenHashSet();
                    prefs.getUidxIidxs(uidx).forEachRemaining(uidxIidxs::add);

                    List<FMInstance> instances = new ArrayList<>();

                    // adding positive examples
                    uidxIidxs
                            .forEach(iidx -> instances.add(getInstance(uidx, iidx, 1.0)));

                    // adding negative examples
                    rnd.ints(iidxs.size(), 0, iidxs.size()).map(iidxs::getInt)
                            .filter(jidx -> !uidxIidxs.contains(jidx))
                            .distinct()
                            .limit((int) (negativeProp * uidxIidxs.size()))
                            .forEach(jidx -> instances.add(getInstance(uidx, jidx, 0.0)));

                    Collections.shuffle(instances);

                    return instances.stream();
                });
    }

}
