/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    protected Int2DoubleMap getScoresMap(int uidx) {
        Int2DoubleOpenHashMap scoresMap = new Int2DoubleOpenHashMap();
        scoresMap.defaultReturnValue(0.0);
        data.getUidxPreferences(uidx).forEach(jp -> {
            neighborhood.getNeighbors(jp.idx).forEach(is -> {
                double w = pow(is.v, q);
                scoresMap.addTo(is.idx, w * jp.v);
            });
        });

        return scoresMap;
    }

}
