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
package es.uam.eps.ir.ranksys.fast.preference;

/**
 * A user or item preference by indexes.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <O> type of other information
 */
public class IdxPref<O> {

    /**
     * The index of a user or an item.
     */
    public int idx;

    /**
     * The weight (rating, play count, etc.) of the preference.
     */
    public double v;

    /**
     * Other information (such as an access log, context) of the preference.
     */
    public O o;

    /**
     * Constructor.
     *
     * @param idx index of user or item for which the preference is expressed
     * @param value weight of the preference
     * @param other other information of the preference
     */
    public IdxPref(int idx, double value, O other) {
        this.idx = idx;
        this.v = value;
        this.o = other;
    }

}
