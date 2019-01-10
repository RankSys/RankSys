/*
 * Copyright (C) 2018 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.tuples;

import java.io.Serializable;
import java.util.Objects;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/**
 * Tuple of object-object.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <T> Type of the object
 */
public class Tuple2oo<T> implements Comparable<Tuple2oo<T>>, Serializable, Cloneable 
{
    /**
     * First value (object).
     */
    public final T v1;

    /**
     * Second value (double).
     */
    public final T v2;

    /**
     * Constructor from a jOOL tuple.
     *
     * @param tuple tuple to be copied.
     */
    public Tuple2oo(Tuple2<T, T> tuple) 
    {
        this(tuple.v1, tuple.v2);
    }

    /**
     * Constructor from an object-double tuple.
     *
     * @param tuple tuple to be copied
     */
    public Tuple2oo(Tuple2oo<T> tuple) 
    {
        this(tuple.v1, tuple.v2);
    }

    /**
     * Constructor from separate object and double values.
     *
     * @param v1 object value
     * @param v2 double value
     */
    public Tuple2oo(T v1, T v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * Returns the first element (object).
     *
     * @return first element (object).
     */
    public T v1() {
        return v1;
    }

    /**
     * Returns the second element (double).
     *
     * @return second element (double).
     */
    public T v2() {
        return v2;
    }

    /**
     * Converts the tuple into a jOOL tuple.
     *
     * @return jOOL tuple
     */
    public Tuple2<T, T> asTuple() {
        return Tuple.tuple(v1, v2);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Tuple2oo<T> other) {
        int result;

        result = ((Comparable<T>) v1).compareTo(other.v1);
        if (result != 0) {
            return result;
        }
        result = ((Comparable<T>) v2).compareTo(other.v2);
        if (result != 0) {
            return result;
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.v1);
        hash = 83 * hash + Objects.hashCode(this.v2);
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
        final Tuple2oo<?> other = (Tuple2oo<?>) obj;
        if (!Objects.equals(this.v2,other.v2)) {
            return false;
        }
        return Objects.equals(this.v1, other.v1);
    }
}
