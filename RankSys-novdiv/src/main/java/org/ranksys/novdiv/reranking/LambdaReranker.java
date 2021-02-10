/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.reranking;

import org.ranksys.core.Recommendation;
import static org.ranksys.novdiv.reranking.PermutationReranker.getBasePerm;
import org.ranksys.core.util.Stats;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.Math.min;
import org.ranksys.core.util.tuples.Tuple2od;
import org.ranksys.novdiv.normalizer.Normalizer;

/**
 * Linear combination re-ranker that combines the original score of the input 
 * recommendation and a novelty component.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class LambdaReranker<U, I> extends GreedyReranker<U, I> {

    /**
     * Trade-off parameter of the linear combination.
     */
    protected final double lambda;
    /**
     * Function for normalizing the scores.
     */
    private final Supplier<Normalizer<I>> norm;

    /**
     * Constructor.
     *
     * @param lambda trade-off parameter of the linear combination
     * @param cutoff how many items are re-ranked by the greedy selection.
     * @param norm supplier for normalizing functions.
     */
    public LambdaReranker(double lambda, int cutoff, Supplier<Normalizer<I>> norm) {
        super(cutoff);
        this.lambda = lambda;
        this.norm = norm;
    }

    /**
     * Returns the permutation that is applied to the input recommendation
     * to generate the re-ranked recommendation.
     *
     * @param recommendation input recommendation
     * @param maxLength maximum length of the permutation
     * @return permutation that encodes the re-ranking
     */
    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation, int maxLength) {
        if (lambda == 0.0) {
            return getBasePerm(min(maxLength, recommendation.getItems().size()));
        } else {
            return super.rerankPermutation(recommendation, maxLength);
        }
    }

    @Override
    protected abstract GreedyUserReranker<U, I> getUserReranker(Recommendation<U, I> recommendation, int maxLength);

    /**
     * User re-ranker for {@link LambdaReranker}.
     */
    protected abstract class LambdaUserReranker extends GreedyUserReranker<U, I> {

        /**
         * Statistics about relevance scores.
         */
        protected Normalizer<I> relStats;

        /**
         * Statistics about novelty scores.
         */
        protected Normalizer<I> novStats;

        /**
         * Map of the novelty of each item.
         */
        protected Object2DoubleMap<I> novMap;

        /**
         * Constructor.
         *
         * @param recommendation input recommendation
         * @param maxLength maximum length of the re-ranked recommendation
         */
        public LambdaUserReranker(Recommendation<U, I> recommendation, int maxLength) {
            super(recommendation, maxLength);
        }

        @Override
        protected int selectItem(IntSortedSet remainingI, List<Tuple2od<I>> list) {
            novMap = new Object2DoubleOpenHashMap<>();
            relStats = norm.get();
            novStats = norm.get();
            remainingI.intStream().forEach(i ->
            {
                Tuple2od<I> itemValue = list.get(i);
                double nov = nov(itemValue);
                novMap.put(itemValue.v1, nov);

                relStats.add(itemValue.v1, itemValue.v2);
                novStats.add(itemValue.v1, nov);
            });
            return super.selectItem(remainingI, list);
        }

        @Override
        protected double value(Tuple2od<I> iv) {
            return (1 - lambda) * relStats.norm(iv.v1, iv.v2) + lambda*novStats.norm(iv.v1, novMap.getDouble(iv.v1));
        }

        /**
         * Returns the novelty score of an item.
         *
         * @param iv item-relevance pair
         * @return the novelty of the item
         */
        protected abstract double nov(Tuple2od<I> iv);
    }
}
