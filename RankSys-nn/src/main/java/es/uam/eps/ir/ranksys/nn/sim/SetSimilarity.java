/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.fast.utils.map.Int2IntDirectMap;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Set similarity. Based on the intersection of item/user profiles as sets.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class SetSimilarity implements Similarity {

    /**
     * User-item preferences.
     */
    protected final FastPreferenceData<?, ?> preferences;

    protected final Supplier<? extends Int2IntMap> mapSupplier;
    protected final BiConsumer<Int2IntMap, Integer> mapAdder;
    protected final IntFunction<Int2IntMap> intersection;

    /**
     * Constructor.
     *
     * @param preferences preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public SetSimilarity(FastPreferenceData<?, ?> preferences, boolean dense) {
        this.preferences = preferences;

        if (dense) {
            this.mapSupplier = () -> new Int2IntDirectMap();
            this.mapAdder = (m, uidx) -> ((Int2IntDirectMap) m).addTo(uidx, 1);
        } else {
            this.mapSupplier = () -> new Int2IntOpenHashMap();
            this.mapAdder = (m, uidx) -> ((Int2IntOpenHashMap) m).addTo(uidx, 1);
        }

        if (preferences.useIteratorsPreferentially()) {
            this.intersection = uidx -> getIteratorsIntersectionMap(uidx);
        } else {
            this.intersection = uidx -> getStreamsIntersectionMap(uidx);
        }
    }

    @Override
    public IntToDoubleFunction similarity(int idx1) {
        IntSet set = new IntOpenHashSet();
        preferences.getUidxPreferences(idx1).map(IdxPref::v1).forEach(set::add);

        return idx2 -> {
            int coo = (int) preferences.getUidxPreferences(idx2)
                    .map(IdxPref::v1)
                    .filter(set::contains)
                    .count();

            return sim(coo, set.size(), preferences.numItems(idx2));
        };
    }

    private Int2IntMap getStreamsIntersectionMap(int idx1) {
        Int2IntMap intersectionMap = mapSupplier.get();

        preferences.getUidxPreferences(idx1).forEach(ip -> {
            preferences.getIidxPreferences(ip.v1).forEach(up -> {
                mapAdder.accept(intersectionMap, up.v1);
            });
        });

        intersectionMap.remove(idx1);

        return intersectionMap;
    }

    private Int2IntMap getIteratorsIntersectionMap(int uidx) {
        Int2IntMap intersectionMap = mapSupplier.get();

        IntIterator iidxs = preferences.getUidxIidxs(uidx);
        while (iidxs.hasNext()) {
            IntIterator vidxs = preferences.getIidxUidxs(iidxs.nextInt());
            while (vidxs.hasNext()) {
                mapAdder.accept(intersectionMap, vidxs.nextInt());
            }
        }

        intersectionMap.remove(uidx);

        return intersectionMap;
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx1) {
        int na = preferences.numItems(idx1);

        return intersection.apply(idx1).int2IntEntrySet().stream()
                .map(e -> {
                    int idx2 = e.getIntKey();
                    int coo = e.getIntValue();
                    int nb = preferences.numItems(idx2);
                    return tuple(idx2, sim(coo, na, nb));
                });
    }

    /**
     * Calculates the similarity value.
     *
     * @param intersectionSize size of the intersection of sets
     * @param na size of the first set
     * @param nb size of the second set
     * @return similarity value
     */
    protected abstract double sim(int intersectionSize, int na, int nb);
}
