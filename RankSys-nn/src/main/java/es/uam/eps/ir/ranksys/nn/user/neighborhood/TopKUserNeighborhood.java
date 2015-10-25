/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.user.neighborhood;

import es.uam.eps.ir.ranksys.nn.neighborhood.TopKNeighborhood;
import es.uam.eps.ir.ranksys.nn.user.sim.UserSimilarity;

/**
 * User top-K neighborhood. See {@link TopKNeighborhood}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public class TopKUserNeighborhood<U> extends UserNeighborhood<U> {

    /**
     * Constructor.
     *
     * @param sim user similarity
     * @param k maximum size of neighborhood
     */
    public TopKUserNeighborhood(UserSimilarity<U> sim, int k) {
        super(sim, new TopKNeighborhood(sim, k));
    }
}
