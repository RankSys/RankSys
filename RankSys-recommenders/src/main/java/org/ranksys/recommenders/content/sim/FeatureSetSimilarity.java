/* 
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content.sim;

import it.unimi.dsi.fastutil.ints.*;
import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.core.feature.user.fast.FastUserFeatureData;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2io;

import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static org.ranksys.core.util.tuples.Tuples.tuple;

/**
 * Set similarity. Based on the inner product of item/user profiles as sets.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 */
public abstract class FeatureSetSimilarity extends FeatureSimilarity
{
    /**
     * If true, dense vectors are used to calculate similarities.
     */
    protected final boolean dense;

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param itemFeatureData feature data for the items
     * @param dense true for array-based calculations, false to map-based
     */
     public FeatureSetSimilarity(FastItemFeatureData<?,?, Double> itemFeatureData, boolean dense)
     {
         this(null, itemFeatureData, dense);
     }

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param userFeatureData feature data for the users
     * @param dense true for array-based calculations, false to map-based
     */
    public FeatureSetSimilarity(FastUserFeatureData<?,?, Double> userFeatureData, boolean dense)
    {
        this(userFeatureData, null, dense);
    }

    /**
     * Constructor. Uses maps for internal calculation.
     *
     * @param userFeatureData feature data for the users
     * @param dense true for array-based calculations, false to map-based
     */
    public FeatureSetSimilarity(FastUserFeatureData<?,?, Double> userFeatureData, FastItemFeatureData<?,?, Double> itemFeatureData, boolean dense)
    {
        super(userFeatureData, itemFeatureData);
        assert userFeatureData != null || itemFeatureData != null;
        this.dense = dense;
    }

    @Override
    public IntToDoubleFunction similarity(int idx1)
    {
        IntSet set = new IntOpenHashSet();

        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;
        boolean check = firstIsUser || secondIsItem;
        assert check;

        // First, find the norm:
        double norm2A;
        if (firstIsUser) // Either sim(u,v) or sim(u,i)
        {
            userFeatData.getUidxFeatures(idx1).mapToInt(Tuple2io::v1).forEach(set::add);
        }
        else// sim(i,j)
        {
            itemFeatData.getIidxFeatures(idx1).mapToInt(Tuple2io::v1).forEach(set::add);
        }

        if (secondIsItem) // sim(u,i) or sim(i,j)
        {
            return idx2 -> {
                int coo = (int) itemFeatData.getIidxFeatures(idx2).mapToInt(Tuple2io::v1).filter(set::contains).count();
                return sim(coo, set.size(), itemFeatData.numFeatures(idx2));
            };
        }
        else // sim(u,v)
        {
            return idx2 -> {
                int coo = (int) userFeatData.getUidxFeatures(idx2).mapToInt(Tuple2io::v1).filter(set::contains).count();
                return sim(coo, set.size(), userFeatData.numFeatures(idx2));
            };
        }

    }

    private Int2IntMap getIntersectionMap(int idx) {
        Int2IntOpenHashMap intersectionMap = new Int2IntOpenHashMap();
        intersectionMap.defaultReturnValue(0);

        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;

        if(firstIsUser && secondIsItem)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    intersectionMap.addTo(ip.v1, 1)
                )
            );
        }
        else if(firstIsUser)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                userFeatData.getFidxUsers(fp.v1).forEach(up ->
                    intersectionMap.addTo(up.v1, 1)
                )
            );
            intersectionMap.remove(idx);
        }
        else if(secondIsItem)
        {
            itemFeatData.getIidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    intersectionMap.addTo(ip.v1, 1)
                )
            );
            intersectionMap.remove(idx);
        }

        return intersectionMap;
    }

    private int[] getIntersectionArray(int idx) {
        int[] intersectionArray;

        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;
        boolean check = firstIsUser || secondIsItem;
        assert check;

        if (secondIsItem)
        {
            intersectionArray = new int[itemFeatData.numItems()];
        }
        else
        {
            intersectionArray = new int[userFeatData.numUsers()];
        }

        if(firstIsUser && secondIsItem)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    intersectionArray[ip.v1]++
                )
            );
        }
        else if(firstIsUser)
        {
            userFeatData.getUidxFeatures(idx).forEach(fp ->
                userFeatData.getFidxUsers(fp.v1).forEach(up ->
                    intersectionArray[up.v1]++
                )
            );
            intersectionArray[idx] = 0;
        }
        else
        {
            itemFeatData.getIidxFeatures(idx).forEach(fp ->
                itemFeatData.getFidxItems(fp.v1).forEach(ip ->
                    intersectionArray[ip.v1]++
                )
            );
            intersectionArray[idx] = 0;
        }

        return intersectionArray;
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx1) {

        boolean firstIsUser = userFeatData != null;
        boolean secondIsItem = itemFeatData != null;
        boolean check = userFeatData != null || itemFeatData != null;
        assert check;

        int na = firstIsUser ? userFeatData.numFeatures(idx1) : itemFeatData.numFeatures(idx1);
        if(dense)
        {
            int[] intersectionArray = getIntersectionArray(idx1);
            return range(0, intersectionArray.length)
                    .filter(i -> intersectionArray[i] != 0)
                    .mapToObj(i -> tuple(i, sim(intersectionArray[i], na, secondIsItem ?itemFeatData.numFeatures(i) : userFeatData.numFeatures(i))));
        }
        else
        {
            return this.getIntersectionMap(idx1).int2IntEntrySet().stream()
                    .map(e -> {
                        int idx2 = e.getIntKey();
                        int coo = e.getIntValue();
                        int nb = secondIsItem ? itemFeatData.numFeatures(idx2) : userFeatData.numFeatures(idx2);
                        return tuple(idx2, sim(coo, na, nb));
                    });
        }
    }

    /**
     * Calculates the similarity value.
     *
     * @param intersectionSize size of the intersection between the two sets.
     * @param na               size of the first set.
     * @param nb               size of the second set.
     * @return similarity value
     */
    protected abstract double sim(int intersectionSize, int na, int nb);
}
