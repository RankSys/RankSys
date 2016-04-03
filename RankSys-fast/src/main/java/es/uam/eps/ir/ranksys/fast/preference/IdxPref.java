/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import java.io.Serializable;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * A user or item preference by indexes.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 */
public class IdxPref extends Tuple2id implements Serializable {

    /**
     * Constructor with both values.
     *
     * @param idx the index
     * @param v the double
     */
    public IdxPref(int idx, double v) {
        super(idx, v);
    }

    public IdxPref(Tuple2id tuple) {
        super(tuple);
    }

}
