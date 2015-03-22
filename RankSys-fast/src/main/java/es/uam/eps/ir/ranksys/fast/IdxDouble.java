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

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

/**
 * A pair of a user/item/feature index and a double.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class IdxDouble implements Int2DoubleMap.Entry{

    /**
     * User/item/feature index.
     */
    public final int idx;

    /**
     * The double.
     */
    public final double v;

    /**
     * Constructor.
     *
     * @param idx index
     * @param v double
     */
    public IdxDouble(int idx, double v) {
        this.idx = idx;
        this.v = v;
    }
    
    /**
     * Constructor that copies an index-double entry.
     *
     * @param e entry whose contents are copied
     */
    public IdxDouble(Int2DoubleMap.Entry e) {
        this(e.getIntKey(), e.getDoubleValue());
    }

    @Override
    public int getIntKey() {
        return idx;
    }

    @Override
    public double setValue(double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getDoubleValue() {
        return v;
    }

    @Override
    public Integer getKey() {
        return idx;
    }

    @Override
    public Double getValue() {
        return v;
    }

    @Override
    public Double setValue(Double value) {
        throw new UnsupportedOperationException();
    }
}
