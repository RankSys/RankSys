/*
 * Copyright (C) 2021 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.recommenders.content;

import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import org.ranksys.core.feature.item.fast.FastItemFeatureData;
import org.ranksys.core.feature.user.fast.MapFastUserFeatureData;
import org.ranksys.core.index.fast.FastFeatureIndex;
import org.ranksys.core.index.fast.FastUserIndex;
import org.ranksys.core.preference.fast.FastPreferenceData;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple user profile. Feature values for the users are simply built as the weighted sum
 * of the features of the items rated by the user (weighted using the rating values).
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 *
 * @param <U> type of the users.
 * @param <F> type of the features.
 */
public class SimpleUserProfile<U, F> extends MapFastUserFeatureData<U,F,Double>
{
    /**
     * Constructor.
     *
     * @param uidxMap map containing the feature-value pairs for each users.
     * @param fidxMap map containing the user-value pairs for each feature.
     * @param ui      user index
     * @param fi      feature index
     */
    protected SimpleUserProfile(Map<Integer, Map<Integer, Double>> uidxMap, Map<Integer, Map<Integer, Double>> fidxMap, FastUserIndex<U> ui, FastFeatureIndex<F> fi)
    {
        super(uidxMap, fidxMap, ui, fi);
    }

    /**
     * Loads a user profile from the preference data and item features.
     *
     * @param <U> type of the users.
     * @param <I> type of the items.
     * @param <F> type of the features.
     *
     * @param prefData preference data.
     * @param featData feature data.
     *
     * @return the feature data for the user.
     */
    public static <U,I,F> SimpleUserProfile<U,F> load(FastPreferenceData<U,I> prefData, FastItemFeatureData<I,F,Double> featData)
    {
        Map<Integer, Map<Integer, Double>> uidxMap = new HashMap<>();
        Map<Integer, Map<Integer, Double>> fidxMap = new HashMap<>();

        featData.getFidxWithItems().forEach(fidx ->
        {
            Int2DoubleOpenHashMap fMap = new Int2DoubleOpenHashMap();
            featData.getFidxItems(fidx).forEach(item ->
                prefData.getIidxPreferences(item.v1).forEach(user ->
                    fMap.addTo(user.v1, user.v2*item.v2)
                )
            );
            fidxMap.put(fidx, fMap);
        });

        fidxMap.forEach((fidx, fMap) ->
            fMap.forEach((uidx, value) ->
            {
                if(uidxMap.containsKey(uidx))
                {
                    uidxMap.put(uidx, new HashMap<>());
                }
                Map<Integer, Double> uMap = uidxMap.get(uidx);
                uMap.put(fidx, value);
            })
        );

        return new SimpleUserProfile<>(uidxMap, fidxMap, prefData, featData);
    }
}
