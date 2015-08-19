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

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

/**
 * A user or item preference by indexes.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 */
public class IdxPref extends IdxDouble {

    /**
     * Empty constructor.
     */
    public IdxPref() {
    }

    /**
     * Constructor with both values.
     *
     * @param idx the index
     * @param v the double
     */
    public IdxPref(int idx, double v) {
        super(idx, v);
    }

    /**
     * Constructor from a Int2Double entry.
     *
     * @param e int-double entry
     */
    public IdxPref(Int2DoubleMap.Entry e) {
        super(e);
    }

    @Override
    public IdxPref refill(int idx, double v) {
        this.idx = idx;
        this.v = v;
        return this;
    }

    /**
     * Re-fills the IdxPref object (only the idx) and returns itself.
     * 
     * This is meant for binary data.
     *
     * @param idx index
     * @return this
     */
    public IdxPref refill(int idx) {
        this.idx = idx;
        return this;
    }
}
