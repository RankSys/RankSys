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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generic recommender runner. This class handles the print of the output.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class AbstractRecommenderRunner<U, I> implements RecommenderRunner<U, I> {

    private static final Logger LOG = Logger.getLogger(AbstractRecommenderRunner.class.getName());

    private final List<U> users;

    /**
     * Constructor.
     *
     * @param users target users for which recommendations are generated
     */
    public AbstractRecommenderRunner(Stream<U> users) {
        this.users = users.sorted().collect(Collectors.toList());
    }

    /**
     * Prints the recommendations.
     *
     * @param recProvider function that provides the recommendations by calling a recommender
     * @param consumer recommendation consumer
     */
    protected void run(Function<U, Recommendation<U, I>> recProvider, Consumer<Recommendation<U, I>> consumer) {
        Map<U, Recommendation<U, I>> pendingRecommendations = new HashMap<>();
        List<U> usersAux = new ArrayList<>(users);

        users.parallelStream()
                .map(user -> recProvider.apply(user))
                .forEachOrdered(recommendation -> {
                    if (recommendation.getUser().equals(usersAux.get(0))) {
                        consumer.accept(recommendation);
                        usersAux.remove(0);

                        while (!usersAux.isEmpty() && pendingRecommendations.containsKey(usersAux.get(0))) {
                            recommendation = pendingRecommendations.get(usersAux.get(0));
                            consumer.accept(recommendation);
                            usersAux.remove(0);
                        }
                    } else {
                        pendingRecommendations.put(recommendation.getUser(), recommendation);
                    }
                });

        usersAux.stream().map(pendingRecommendations::get).forEach(consumer);
    }
}
