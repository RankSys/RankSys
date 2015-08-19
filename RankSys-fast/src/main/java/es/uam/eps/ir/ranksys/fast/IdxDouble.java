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
public class IdxDouble implements Int2DoubleMap.Entry {

    /**
     * User/item/feature index.
     */
    public int idx;

    /**
     * The double.
     */
    public double v;

    /**
     * Empty constructor.
     */
    public IdxDouble() {
    }

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
     * Re-fills the IdxDouble object and returns itself.
     *
     * @param idx the index
     * @param v the double
     * @return this
     */
    public IdxDouble refill(int idx, double v) {
        this.idx = idx;
        this.v = v;
        return this;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.idx;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.v) ^ (Double.doubleToLongBits(this.v) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IdxDouble other = (IdxDouble) obj;
        if (this.idx != other.idx) {
            return false;
        }
        return Double.doubleToLongBits(this.v) == Double.doubleToLongBits(other.v);
    }

}
