/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.user;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.rec.fast.FastRankingRecommender;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import static java.lang.Math.pow;

/**
 * User-based nearest neighbors recommender.
 * 
 * F. Aiolli. Efficient Top-N Recommendation for Very Large Scale Binary Rated
 * Datasets. RecSys 2013.
 * 
 * Paolo Cremonesi, Yehuda Koren, and Roberto Turrin. Performance of 
 * recommender algorithms on top-n recommendation tasks. RecSys 2010.
 * 
 * C. Desrosiers, G. Karypis. A comprehensive survey of neighborhood-based 
 * recommendation methods. Recommender Systems Handbook.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class UserNeighborhoodRecommender<U, I> extends FastRankingRecommender<U, I> {

    /**
     * Preference data.
     */
    protected final FastPreferenceData<U, I> data;

    /**
     * User neighborhood.
     */
    protected final UserNeighborhood<U> neighborhood;

    /**
     * Exponent of the similarity.
     */
    protected final int q;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param neighborhood user neighborhood
     * @param q exponent of the similarity
     */
    public UserNeighborhoodRecommender(FastPreferenceData<U, I> data, UserNeighborhood<U> neighborhood, int q) {
        super(data, data);
        this.data = data;
        this.neighborhood = neighborhood;
        this.q = q;
    }

    /**
     * Returns a map of item-score pairs.
     *
     * @param uidx index of the user whose scores are predicted
     * @return a map of item-score pairs
     */
    @Override
    public Int2DoubleMap getScoresMap(int uidx) {
        Int2DoubleOpenHashMap scoresMap = new Int2DoubleOpenHashMap();
        scoresMap.defaultReturnValue(0.0);
        neighborhood.getNeighbors(uidx).forEach(vs -> {
            double w = pow(vs.v2, q);
            data.getUidxPreferences(vs.v1).forEach(iv -> {
                double p = w * iv.v2;
                scoresMap.addTo(iv.v1, p);
            });
        });

        return scoresMap;
    }
}
