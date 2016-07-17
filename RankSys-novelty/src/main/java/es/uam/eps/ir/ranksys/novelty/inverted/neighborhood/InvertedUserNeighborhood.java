/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.inverted.neighborhood;

import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * Inverted user neighborhood. See {@link InvertedNeighborhood}
 * 
 * S. Vargas and P. Castells. Improving sales diversity by recommending
 * users to items.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 */
public class InvertedUserNeighborhood<U> extends UserNeighborhood<U> {

    /**
     * Constructor.
     *
     * @param neighborhood original neighborhood to be inverted
     * @param filter determines which users require inverted neighborhoods
     */
    public InvertedUserNeighborhood(UserNeighborhood<U> neighborhood, Predicate<U> filter) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numUsers(), neighborhood.neighborhood(), uidx -> filter.test(neighborhood.uidx2user(uidx))));
    }
    
    /**
     * Constructor - fast version.
     *
     * @param neighborhood original neighborhood to be inverted
     * @param filter determines which users require inverted neighborhoods
     */
    public InvertedUserNeighborhood(UserNeighborhood<U> neighborhood, IntPredicate filter) {
        super(neighborhood, new InvertedNeighborhood(neighborhood.numUsers(), neighborhood.neighborhood(), filter));
    }
}
