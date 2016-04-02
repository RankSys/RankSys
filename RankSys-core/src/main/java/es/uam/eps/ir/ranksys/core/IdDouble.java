/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.io.Serializable;
import java.util.Objects;

/**
 * A pair of a user/item/feature ID and a double.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the user/item/feature
 */
public class IdDouble<I> implements Object2DoubleMap.Entry<I>, Serializable {

    /**
     * The ID.
     */
    public I id;

    /**
     * The double.
     */
    public double v;

    /**
     * Empty constructor.
     */
    public IdDouble() {
    }

    /**
     * Constructs an ID-double pair.
     *
     * @param id the ID
     * @param v the double
     */
    public IdDouble(I id, double v) {
        this.id = id;
        this.v = v;
    }

    /**
     * Re-fills the IdDouble object and returns itself.
     *
     * @param id the ID
     * @param v the double
     * @return this
     */
    public IdDouble refill(I id, double v) {
        this.id = id;
        this.v = v;
        return this;
    }

    /**
     * Constructs an ID-double pair by copying an existing pair.
     *
     * @param e ID-double pair.
     */
    public IdDouble(Object2DoubleMap.Entry<I> e) {
        this(e.getKey(), e.getDoubleValue());
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
    public I getKey() {
        return id;
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
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.v) ^ (Double.doubleToLongBits(this.v) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IdDouble<?> other = (IdDouble<?>) obj;
        if (Double.doubleToLongBits(this.v) != Double.doubleToLongBits(other.v)) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

}
