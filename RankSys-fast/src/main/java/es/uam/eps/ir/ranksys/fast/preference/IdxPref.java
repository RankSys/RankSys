/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
