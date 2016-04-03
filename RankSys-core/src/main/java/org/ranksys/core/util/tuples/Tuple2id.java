/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.tuples;

import java.io.Serializable;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class Tuple2id implements Comparable<Tuple2id>, Serializable, Cloneable {

    public final int v1;
    public final double v2;
    
    public Tuple2id(Tuple2<Integer, Double> tuple) {
        this(tuple.v1, tuple.v2);
    }

    public Tuple2id(Tuple2id tuple) {
        this(tuple.v1, tuple.v2);
    }

    public Tuple2id(int v1, double v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public int v1() {
        return v1;
    }

    public double v2() {
        return v2;
    }

    public Tuple2<Integer, Double> asTuple() {
        return Tuple.tuple(v1, v2);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Tuple2id other) {
        int result;

        result = Integer.compare(v1, other.v1);
        if (result != 0) {
            return result;
        }
        result = Double.compare(v2, other.v2);
        if (result != 0) {
            return result;
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.v1;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.v2) ^ (Double.doubleToLongBits(this.v2) >>> 32));
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
        final Tuple2id other = (Tuple2id) obj;
        if (this.v1 != other.v1) {
            return false;
        }
        return Double.doubleToLongBits(this.v2) == Double.doubleToLongBits(other.v2);
    }

}
