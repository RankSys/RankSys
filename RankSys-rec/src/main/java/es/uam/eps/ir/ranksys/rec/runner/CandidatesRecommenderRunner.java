/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.core.format.RecommendationFormat;
import es.uam.eps.ir.ranksys.rec.Recommender;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Recommender runner that generates recommendations using the candidate
 * recommender method.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class CandidatesRecommenderRunner<U, I> extends AbstractRecommenderRunner<U, I> {

    private final Function<U, List<I>> candidatesSupplier;

    /**
     * Constructor.
     *
     * @param users target users for which recommendations are generated
     * @param format output recommendation format
     * @param candidatesSupplier function that provide the candidate items for
     * each user
     */
    public CandidatesRecommenderRunner(Set<U> users, RecommendationFormat<U, I> format, Function<U, List<I>> candidatesSupplier) {
        super(users.stream(), format);
        this.candidatesSupplier = candidatesSupplier;
    }

    @Override
    public void run(Recommender<U, I> recommender, OutputStream out) throws IOException {
        run(user -> recommender.getRecommendation(user, candidatesSupplier.apply(user).stream()), out);
    }

}
