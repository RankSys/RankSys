/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.util.function.Consumer;

/**
 * Recommender runner. Implementing classes provide convenient ways of 
 * generating recommendations according to a specific recommendation 
 * evaluation methodology and storing/printing them.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public interface RecommenderRunner<U, I> {

    /**
     * Runs the recommender and prints the recommendations to an output
     * stream.
     *
     * @param recommender recommender to be run
     * @param consumer recommendation consumer
     */
    public void run(Recommender<U, I> recommender, Consumer<Recommendation<U, I>> consumer);
}
