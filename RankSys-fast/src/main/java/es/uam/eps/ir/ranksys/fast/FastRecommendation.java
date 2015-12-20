/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast;

import java.util.List;

/**
 * Fast recommendation, where users and items are identified by index.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FastRecommendation {

    private final int uidx;
    private final List<IdxDouble> iidxs;

    /**
     * Constructor.
     *
     * @param uidx index of the user
     * @param iidxs list of item-score pairs identified by index.
     */
    public FastRecommendation(int uidx, List<IdxDouble> iidxs) {
        this.uidx = uidx;
        this.iidxs = iidxs;
    }

    /**
     * Returns the index of the user for which the recommendation is issued.
     *
     * @return the index of the user
     */
    public int getUidx() {
        return uidx;
    }

    /**
     * Returns the list of item-score pairs identified by index.
     *
     * @return the list of item-score pairs
     */
    public List<IdxDouble> getIidxs() {
        return iidxs;
    }
}
