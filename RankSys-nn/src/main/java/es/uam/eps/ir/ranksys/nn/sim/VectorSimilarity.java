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
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import org.ranksys.core.util.tuples.Tuple2id;

import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.fast.utils.map.Int2DoubleDirectMap;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Vector similarity. Based on the inner product of item/user profiles as vectors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class VectorSimilarity implements Similarity {

    /**
     * User-item preferences.
     */
    protected final FastPreferenceData<?, ?> preferences;

    protected final Supplier<? extends Int2DoubleMap> mapSupplier;
    protected final BiConsumer<Int2DoubleMap, Tuple2id> mapAdder;
    protected final IntFunction<Int2DoubleMap> intersection;
    protected final Int2DoubleMap norm;

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param preferences preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public VectorSimilarity(FastPreferenceData<?, ?> preferences, boolean dense) {
        this.preferences = preferences;

        if (dense) {
            this.mapSupplier = () -> new Int2DoubleDirectMap();
            this.mapAdder = (m, t) -> ((Int2DoubleDirectMap) m).addTo(t.v1, t.v2);
            this.norm = new Int2DoubleDirectMap();
        } else {
            this.mapSupplier = () -> new Int2DoubleOpenHashMap();
            this.mapAdder = (m, t) -> ((Int2DoubleOpenHashMap) m).addTo(t.v1, t.v2);
            this.norm = new Int2DoubleOpenHashMap();
        }

        preferences.getUidxWithPreferences().forEach(idx -> norm.put(idx, getNorm2(idx)));
    }

    @Override
    public IntToDoubleFunction similarity(int idx1) {
        Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();
        preferences.getUidxPreferences(idx1).forEach(iv -> map.put(iv.v1, iv.v2));

        double norm2A = norm.get(idx1);

        return idx2 -> {
            double product = preferences.getUidxPreferences(idx2)
                    .mapToDouble(iv -> iv.v2 * map.get(iv.v1))
                    .sum();

            return sim(product, norm2A, norm.get(idx2));
        };
    }

    private Int2DoubleMap getProductMap(int uidx) {
        Int2DoubleOpenHashMap productMap = new Int2DoubleOpenHashMap();
        productMap.defaultReturnValue(0.0);

        if (preferences.useIteratorsPreferentially()) {
            IntIterator iidxs = preferences.getUidxIidxs(uidx);
            DoubleIterator ivs = preferences.getUidxVs(uidx);
            while (iidxs.hasNext()) {
                int iidx = iidxs.nextInt();
                double iv = ivs.nextDouble();
                IntIterator vidxs = preferences.getIidxUidxs(iidx);
                DoubleIterator vvs = preferences.getIidxVs(iidx);
                while (vidxs.hasNext()) {
                    productMap.addTo(vidxs.nextInt(), iv * vvs.nextDouble());
                }
            }
        } else {
            preferences.getUidxPreferences(uidx).forEach(ip -> {
                preferences.getIidxPreferences(ip.v1).forEach(up -> {
                    productMap.addTo(up.v1, ip.v2 * up.v2);
                });
            });
        }

        productMap.remove(uidx);

        return productMap;
    }

    private double getNorm2(int uidx) {
        if (preferences.useIteratorsPreferentially()) {
            DoubleIterator ivs = preferences.getUidxVs(uidx);
            double sum = 0;
            while (ivs.hasNext()) {
                double iv = ivs.nextDouble();
                sum += iv * iv;
            }
            return sum;
        } else {
            return preferences.getUidxPreferences(uidx)
                    .mapToDouble(IdxPref::v2)
                    .map(x -> x * x)
                    .sum();
        }
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx1) {
        double norm2A = norm.get(idx1);

        return getProductMap(idx1).int2DoubleEntrySet().stream()
                .map(e -> {
                    int idx2 = e.getIntKey();
                    double product = e.getDoubleValue();
                    double norm2B = norm.get(idx2);
                    return tuple(idx2, sim(product, norm2A, norm2B));
                });
    }

    /**
     * Calculates the similarity value.
     *
     * @param product value of the inner product of vectors
     * @param norm2A square of the norm of the first vector
     * @param norm2B square of the norm of the second vector
     * @return similarity value
     */
    protected abstract double sim(double product, double norm2A, double norm2B);
}
