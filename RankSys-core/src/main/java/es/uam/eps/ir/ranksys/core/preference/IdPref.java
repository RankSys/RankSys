/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.preference;

import es.uam.eps.ir.ranksys.core.IdDouble;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.io.Serializable;

/**
 * A user or item preference.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> type of the user or item
 */
public class IdPref<I> extends IdDouble<I> implements Serializable {

    /**
     * Empty constructor.
     */
    public IdPref() {
    }

    /**
     * Constructor with both values.
     *
     * @param id the ID
     * @param v the double
     */
    public IdPref(I id, double v) {
        super(id, v);
    }

    /**
     * Constructor from a Object2Double entry.
     *
     * @param e object-double entry
     */
    public IdPref(Object2DoubleMap.Entry<I> e) {
        super(e);
    }

    @Override
    public IdDouble refill(I id, double v) {
        this.id = id;
        this.v = v;
        return this;
    }

}
