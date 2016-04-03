/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.core.util.tuples;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class Tuples {

    public static <T1> Tuple2od<T1> tuple(T1 v1, double v2) {
        return new Tuple2od<>(v1, v2);
    }

    public static <T2> Tuple2io<T2> tuple(int v1, T2 v2) {
        return new Tuple2io<>(v1, v2);
    }

    public static Tuple2id tuple(int v1, double v2) {
        return new Tuple2id(v1, v2);
    }
}
