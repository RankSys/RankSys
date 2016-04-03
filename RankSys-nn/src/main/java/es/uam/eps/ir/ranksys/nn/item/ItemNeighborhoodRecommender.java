/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.rec.fast.FastRankingRecommender;
import es.uam.eps.ir.ranksys.nn.item.neighborhood.ItemNeighborhood;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import static java.lang.Math.pow;

/**
 * Item-based nearest neighbors recommender.
 * 
 * F. Aiolli. Efficient Top-N Recommendation for Very Large Scale Binary Rated
 * Datasets. RecSys 2013.
 * 
 * P. Cremonesi, Y. Koren, and R. Turrin. Performance of 
 * recommender algorithms on top-N recommendation tasks. RecSys 2010.
 * 
 * B. Sarwar, G. Karypis, J. Konstan, and J. Riedl. Item-based collaborative
 * filtering recommendation algorithms. WWW 2001.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ItemNeighborhoodRecommender<U, I> extends FastRankingRecommender<U, I> {

    /**
     * Preference data.
     */
    protected final FastPreferenceData<U, I> data;

    /**
     * Item neighborhoods.
     */
    protected final ItemNeighborhood<I> neighborhood;

    /**
     * Exponent of the similarity.
     */
    protected final int q;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param neighborhood item neighborhood
     * @param q exponent of the similarity
     */
    public ItemNeighborhoodRecommender(FastPreferenceData<U, I> data, ItemNeighborhood<I> neighborhood, int q) {
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
        data.getUidxPreferences(uidx).forEach(jp -> {
            neighborhood.getNeighbors(jp.v1).forEach(is -> {
                double w = pow(is.v2, q);
                scoresMap.addTo(is.v1, w * jp.v2);
            });
        });

        return scoresMap;
    }

}
