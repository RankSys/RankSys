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
package es.uam.eps.ir.ranksys.fast;

import java.util.List;

/**
 * Fast recommendation, where users and items are identified by index.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FastRecommendation {

    private final int uidx;
    private final List<IdxDouble> iidxs;

    /**
     * Constructor.
     *
     * @param uidx index of the user
     * @param iidxs list of item-score pairs identified by index.
     */
    public FastRecommendation(int uidx, List<IdxDouble> iidxs) {
        this.uidx = uidx;
        this.iidxs = iidxs;
    }

    /**
     * Returns the index of the user for which the recommendation is issued.
     *
     * @return the index of the user
     */
    public int getUidx() {
        return uidx;
    }

    /**
     * Returns the list of item-score pairs identified by index.
     *
     * @return the list of item-score pairs
     */
    public List<IdxDouble> getIidxs() {
        return iidxs;
    }
}
