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
import it.unimi.dsi.fastutil.ints.*;
import org.ranksys.core.util.tuples.Tuple2id;

import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Vector similarity. Based on the inner product of item/user profiles as vectors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class AbstractSimilarity implements Similarity {

    /**
     * User-item preferences.
     */
    protected final FastPreferenceData<?, ?> data;

    /**
     * If true, dense vectors are used to calculate similarities.
     */
    protected final boolean dense;

    /**
     * Cached normalization for when dense is false.
     */
    protected final Int2DoubleMap norm2Map;

    /**
     * Cached normalization for when dense is true.
     */
    protected final double[] norm2Array;

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param data  preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public AbstractSimilarity(FastPreferenceData<?, ?> data, boolean dense) {
        this.data = data;
        this.dense = dense;
        if (dense) {
            this.norm2Map = null;
            this.norm2Array = new double[data.numUsers()];
            data.getUidxWithPreferences().forEach(idx -> norm2Array[idx] = getNorm2(idx));
        } else {
            this.norm2Map = new Int2DoubleOpenHashMap();
            this.norm2Array = null;
            norm2Map.defaultReturnValue(0.0);
            data.getUidxWithPreferences().forEach(idx -> norm2Map.put(idx, getNorm2(idx)));
        }
    }

    @Override
    public IntToDoubleFunction similarity(int idx1) {
        Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();
        data.getUidxPreferences(idx1).forEach(iv -> map.put(iv.v1, iv.v2));

        double norm2A = norm2Map.get(idx1);

        return idx2 -> {
            double product = data.getUidxPreferences(idx2)
                    .mapToDouble(iv -> iv.v2 * map.get(iv.v1))
                    .sum();

            return sim(product, norm2A, norm2Map.get(idx2), 0, 0, 0);
        };
    }

    private Int2ObjectMap<Product> getProductMap(int uidx) {
        Int2ObjectMap<Product> productMap = new Int2ObjectOpenHashMap();

        if (data.useIteratorsPreferentially()) {
            IntIterator iidxs = data.getUidxIidxs(uidx);
            DoubleIterator ivs = data.getUidxVs(uidx);
            while (iidxs.hasNext()) {
                int iidx = iidxs.nextInt();
                double iv = ivs.nextDouble();
                IntIterator vidxs = data.getIidxUidxs(iidx);
                DoubleIterator vvs = data.getIidxVs(iidx);
                while (vidxs.hasNext()) {
                    productMap.computeIfAbsent(vidxs.nextInt(), _vidx -> new Product()).add(iv * vvs.nextDouble());
                }
            }
        } else {
            data.getUidxPreferences(uidx).forEach(ip -> {
                data.getIidxPreferences(ip.v1).forEach(up -> {
                    productMap.computeIfAbsent(up.v1, _vidx -> new Product()).add(ip.v2 * up.v2);
                });
            });
        }

        productMap.remove(uidx);

        return productMap;
    }

    private Product[] getProductArray(int uidx) {
        Product[] productArray = new Product[data.numUsers()];

        if (data.useIteratorsPreferentially()) {
            IntIterator iidxs = data.getUidxIidxs(uidx);
            DoubleIterator ivs = data.getUidxVs(uidx);
            while (iidxs.hasNext()) {
                int iidx = iidxs.nextInt();
                double iv = ivs.nextDouble();
                IntIterator vidxs = data.getIidxUidxs(iidx);
                DoubleIterator vvs = data.getIidxVs(iidx);
                while (vidxs.hasNext()) {
                    productArray[vidxs.nextInt()].add(iv * vvs.nextDouble());
                }
            }
        } else {
            data.getUidxPreferences(uidx).forEach(ip -> {
                data.getIidxPreferences(ip.v1).forEach(up -> {
                    productArray[up.v1].add(ip.v2 * up.v2);
                });
            });
        }

        productArray[uidx].reset();

        return productArray;
    }

    private double getNorm2(int uidx) {
        if (data.useIteratorsPreferentially()) {
            DoubleIterator ivs = data.getUidxVs(uidx);
            double sum = 0;
            while (ivs.hasNext()) {
                double iv = ivs.nextDouble();
                sum += iv * iv;
            }
            return sum;
        } else {
            return data.getUidxPreferences(uidx)
                    .mapToDouble(IdxPref::v2)
                    .map(x -> x * x)
                    .sum();
        }
    }

    @Override
    public Stream<Tuple2id> similarElems(int uidx) {
        int sizeA = data.numItems(uidx);

        if (dense) {
            double norm2A = norm2Array[uidx];

            Product[] productArray = getProductArray(uidx);
            return range(0, productArray.length)
                    .filter(vidx -> productArray[vidx].s > 0)
                    .mapToObj(vidx -> {
                        Product product = productArray[vidx];
                        return tuple(vidx, sim(product.v, norm2A, norm2Array[vidx], product.s, sizeA, data.numItems(vidx)));
                    });
        } else {
            double norm2A = norm2Map.get(uidx);

            Int2ObjectMap<Product> productMap = getProductMap(uidx);
            return productMap.int2ObjectEntrySet().stream()
                    .map(e -> {
                        int vidx = e.getIntKey();
                        Product product = e.getValue();
                        return tuple(vidx, sim(product.v, norm2A, norm2Map.get(vidx), product.s, sizeA, data.numItems(vidx)));
                    });
        }
    }

    /**
     * Calculates the similarity value.
     *
     * @param product value of the inner product of vectors
     * @param norm2A  square of the norm of the first vector
     * @param norm2B  square of the norm of the second vector
     * @return similarity value
     */
    protected abstract double sim(double product, double norm2A, double norm2B, int intersectionSize, int sizeA, int sizeB);

    private static class Product {
        public double v = 0.0;
        public int s = 0;

        public void reset() {
            v = 0.0;
            s = 0;
        }

        public void add(double v2) {
            v += v2;
            s++;
        }
    }
}
