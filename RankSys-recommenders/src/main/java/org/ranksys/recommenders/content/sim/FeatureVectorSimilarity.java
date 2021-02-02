/* 
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.sim;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.core.feature.user.fast.FastUserFeatureData;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2io;

import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Vector similarity. Based on the inner product of item/user profiles as vectors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public abstract class FeatureVectorSimilarity extends FeatureSimilarity
{
    /**
     * If true, dense vectors are used to calculate similarities.
     */
    protected final boolean dense;

    /**
     * Cached normalization for when dense is false.
     */
    protected final Int2DoubleMap userNorm2Map;
    /**
     * Cached normalization for when dense is false.
     */
    protected final Int2DoubleMap itemNorm2Map;

    /**
     * Cached normalization for when dense is true.
     */
    protected final double[] userNorm2Array;
    /**
     * Cached normalization for when dense is true.
     */
    protected final double[] itemNorm2Array;

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param itemFeatureData feature data for the items
     * @param dense true for array-based calculations, false to map-based
     */
     public FeatureVectorSimilarity(FastItemFeatureData<?,?, Double> itemFeatureData, boolean dense)
     {
         this(null, itemFeatureData, dense);
     }

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param userFeatureData feature data for the items
     * @param dense true for array-based calculations, false to map-based
     */
    public FeatureVectorSimilarity(FastUserFeatureData<?,?, Double> userFeatureData, boolean dense)
    {
        this(userFeatureData, null, dense);
    }

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param userFeatureData feature data for the items
     * @param dense true for array-based calculations, false to map-based
     */
    public FeatureVectorSimilarity(FastUserFeatureData<?,?, Double> userFeatureData, FastItemFeatureData<?,?, Double> itemFeatureData, boolean dense)
    {
        super(userFeatureData, itemFeatureData);
        assert userFeatureData != null || itemFeatureData != null;
        this.dense = dense;
        if(dense)
        {
            this.userNorm2Map = null;
            this.itemNorm2Map = null;

            if(userFeatureData != null)
            {
                this.userNorm2Array = new double[userFeatureData.numUsers()];
                userFeatureData.getUidxWithFeatures().forEach(idx -> userNorm2Array[idx] = getUserNorm2(idx));
            }
            else
            {
                this.userNorm2Array = null;
            }

            if(itemFeatureData != null)
            {
                this.itemNorm2Array = new double[itemFeatureData.numItems()];
                itemFeatureData.getIidxWithFeatures().forEach(idx -> itemNorm2Array[idx] = getItemNorm2(idx));
            }
            else
            {
                this.itemNorm2Array = null;
            }
        }
        else
        {
            this.userNorm2Array = null;
            this.itemNorm2Array = null;

            if(userFeatureData != null)
            {
                this.userNorm2Map = new Int2DoubleOpenHashMap();
                this.userNorm2Map.defaultReturnValue(0.0);
                userFeatureData.getUidxWithFeatures().forEach(idx -> userNorm2Map.put(idx, getUserNorm2(idx)));
            }
            else
            {
                this.userNorm2Map = null;
            }

            if(itemFeatureData != null)
            {
                this.itemNorm2Map = new Int2DoubleOpenHashMap();
                this.itemNorm2Map.defaultReturnValue(0.0);
                itemFeatureData.getIidxWithFeatures().forEach(idx -> itemNorm2Map.put(idx, getItemNorm2(idx)));
            }
            else
            {
                this.itemNorm2Map = null;
            }
        }
    }

    @Override
    public IntToDoubleFunction similarity(int idx1)
    {
        Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();
        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;
        boolean check = firstIsUser || secondIsItem;
        assert check;

        // First, find the norm:
        double norm2A;
        if (firstIsUser) // Either sim(u,v) or sim(u,i)
        {
            norm2A = dense ? userNorm2Array[idx1] : userNorm2Map.get(idx1);
            userFeatData.getUidxFeatures(idx1).forEach(fv -> map.put(fv.v1, fv.v2.doubleValue()));
        }
        else// sim(i,j)
        {
            norm2A = dense ? itemNorm2Array[idx1] : itemNorm2Map.get(idx1);
            itemFeatData.getIidxFeatures(idx1).forEach(fv -> map.put(fv.v1, fv.v2.doubleValue()));
        }

        if (secondIsItem) // sim(u,i) or sim(i,j)
        {
            return idx2 -> {
                double product = itemFeatData.getIidxFeatures(idx2).mapToDouble(fv -> fv.v2 * map.get(fv.v1)).sum();
                return sim(product, norm2A, dense ? itemNorm2Array[idx2] : itemNorm2Map.get(idx2));
            };
        }
        else // sim(u,v)
        {
            return idx2 -> {
                double product = userFeatData.getUidxFeatures(idx2).mapToDouble(fv -> fv.v2 * map.get(fv.v1)).sum();
                return sim(product, norm2A, dense ? userNorm2Array[idx2] : userNorm2Map.get(idx2));
            };
        }

    }

    private Int2DoubleMap getProductMap(int idx) {
        Int2DoubleOpenHashMap productMap = new Int2DoubleOpenHashMap();
        productMap.defaultReturnValue(0.0);

        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;

        if(firstIsUser && secondIsItem)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    productMap.addTo(ip.v1, ip.v2*fp.v2)
                )
            );
        }
        else if(firstIsUser)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                userFeatData.getFidxUsers(fp.v1).forEach(up ->
                    productMap.addTo(up.v1, up.v2*fp.v2)
                )
            );
            productMap.remove(idx);
        }
        else if(secondIsItem)
        {
            itemFeatData.getIidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    productMap.addTo(ip.v1, ip.v2*fp.v2)
                )
            );
            productMap.remove(idx);
        }

        return productMap;
    }

    private double[] getProductArray(int idx) {
        double[] productArray;

        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;
        boolean check = firstIsUser || secondIsItem;
        assert check;

        if (secondIsItem)
        {
            productArray = new double[itemFeatData.numItems()];
        }
        else
        {
            productArray = new double[userFeatData.numUsers()];
        }

        if(firstIsUser && secondIsItem)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    productArray[ip.v1] += ip.v2*fp.v2
                )
            );
        }
        else if(firstIsUser)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                userFeatData.getFidxUsers(fp.v1).forEach(up ->
                    productArray[up.v1] += up.v2*fp.v2
                )
            );
            productArray[idx] = 0.0;
        }
        else
        {
            itemFeatData.getIidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    productArray[ip.v1] += ip.v2*fp.v2
                )
            );
            productArray[idx] = 0.0;
        }

        return productArray;
    }

    private double getItemNorm2(int iidx) {
        if(itemFeatData == null) return 0.0;
        return itemFeatData.getIidxFeatures(iidx).mapToDouble(Tuple2io::v2).map(x -> x*x).sum();
    }

    private double getUserNorm2(int uidx) {
        if(userFeatData == null) return 0.0;
        return userFeatData.getUidxFeatures(uidx).mapToDouble(Tuple2io::v2).map(x -> x*x).sum();
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx1) {

        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;


        if (dense) {
            double norm2A = firstIsUser ? userNorm2Array[idx1] : itemNorm2Array[idx1];

            double[] productArray = getProductArray(idx1);
            return range(0, productArray.length)
                    .filter(idx2 -> productArray[idx2] != 0.0)
                    .mapToObj(idx2 -> tuple(idx2, sim(productArray[idx2], norm2A, secondIsItem ? itemNorm2Array[idx2] : userNorm2Array[idx2])));
        } else {
            double norm2A = firstIsUser ? userNorm2Map.get(idx1) : itemNorm2Map.get(idx1);

            return getProductMap(idx1).int2DoubleEntrySet().stream()
                    .map(e -> {
                        int idx2 = e.getIntKey();
                        double product = e.getDoubleValue();
                        double norm2B = secondIsItem ? itemNorm2Map.get(idx2) : userNorm2Map.get(idx2);
                        return tuple(idx2, sim(product, norm2A, norm2B));
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
    protected abstract double sim(double product, double norm2A, double norm2B);
}
