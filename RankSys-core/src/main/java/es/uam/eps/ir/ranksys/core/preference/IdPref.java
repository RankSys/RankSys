/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.preference;

import java.io.Serializable;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * A user or item preference.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the user or item
 */
public class IdPref<I> extends Tuple2od<I> implements Serializable {

    /**
     * Constructor with both values.
     *
     * @param id the ID
     * @param v the double
     */
    public IdPref(I id, double v) {
        super(id, v);
    }

    public IdPref(Tuple2od<I> tuple) {
        super(tuple);
    }

}
