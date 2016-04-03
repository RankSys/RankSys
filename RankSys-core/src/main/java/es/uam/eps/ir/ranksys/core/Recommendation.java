/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core;

import java.util.List;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * A recommendation issued to a user.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the user
 * @param <I> type of the items
 */
public class Recommendation<U, I> {

    private final U user;
    private final List<Tuple2od<I>> items;

    /**
     * Constructs the recommendation.
     *
     * @param user the user that receives the recommendation
     * @param items a list of item ID-score pairs sorted by descending score
     */
    public Recommendation(U user, List<Tuple2od<I>> items) {
        this.user = user;
        this.items = items;
    }

    /**
     * Returns the user that receives the recommendation.
     * 
     * @return the ID of the user
     */
    public U getUser() {
        return user;
    }

    /**
     * Returns the list of item-score pairs.
     * 
     * @return a list of item ID-score pairs sorted by descending score
     */
    public List<Tuple2od<I>> getItems() {
        return items;
    }
}
