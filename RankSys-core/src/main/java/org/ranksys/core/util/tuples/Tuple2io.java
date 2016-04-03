/*
 * Copyright (C) 2016 RankSys http://ranksys.org
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
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class Tuple2io<T2> implements Comparable<Tuple2io<T2>>, Serializable, Cloneable {

    public final int v1;
    public final T2 v2;

    public Tuple2io(Tuple2<Integer, T2> tuple) {
        this(tuple.v1, tuple.v2);
    }

    public Tuple2io(Tuple2io<T2> tuple) {
        this(tuple.v1, tuple.v2);
    }

    public Tuple2io(int v1, T2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public int v1() {
        return v1;
    }

    public T2 v2() {
        return v2;
    }

    public Tuple2<Integer, T2> asTuple() {
        return Tuple.tuple(v1, v2);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Tuple2io<T2> other) {
        int result;

        result = Integer.compare(v1, other.v1);
        if (result != 0) {
            return result;
        }
        result = ((Comparable<T2>) v2).compareTo(other.v2);
        if (result != 0) {
            return result;
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.v1;
        hash = 71 * hash + Objects.hashCode(this.v2);
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
        final Tuple2io<?> other = (Tuple2io<?>) obj;
        if (this.v1 != other.v1) {
            return false;
        }
        return Objects.equals(this.v2, other.v2);
    }


}
