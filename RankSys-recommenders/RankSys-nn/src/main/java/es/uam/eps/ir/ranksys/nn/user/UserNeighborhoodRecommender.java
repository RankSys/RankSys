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
package es.uam.eps.ir.ranksys.nn.user;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.rec.fast.FastRankingRecommender;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import static java.lang.Math.pow;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class UserNeighborhoodRecommender<U, I> extends FastRankingRecommender<U, I> {

    protected final FastPreferenceData<U, I, ?> data;
    protected final UserNeighborhood<U> neighborhood;
    protected final int q;

    public UserNeighborhoodRecommender(FastPreferenceData<U, I, ?> data, UserNeighborhood<U> neighborhood, int q) {
        super(data, data);
        this.data = data;
        this.neighborhood = neighborhood;
        this.q = q;
    }

    public UserNeighborhood<U> getNeighborhood() {
        return neighborhood;
    }

    @Override
    protected Int2DoubleMap getScoresMap(int uidx) {
        Int2DoubleOpenHashMap scoresMap = new Int2DoubleOpenHashMap();
        scoresMap.defaultReturnValue(0.0);
        neighborhood.getNeighbors(uidx).forEach(vs -> {
            double w = pow(vs.v, q);
            data.getUidxPreferences(vs.idx).forEach(iv -> {
                double p = w * iv.v;
                scoresMap.addTo(iv.idx, p);
            });
        });

        return scoresMap;
    }
}
