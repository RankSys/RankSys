/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.itemnovelty.reranking;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.core.util.Stats;
import es.uam.eps.ir.ranksys.fast.utils.topn.IntDoubleTopN;
import es.uam.eps.ir.ranksys.novdiv.reranking.PermutationReranker;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import static java.lang.Math.min;
import java.util.List;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Item Novelty re-ranker. It re-ranks the output of a recommendation by re-scoring through a linear combination of the relevance scores and the output of a {@link ItemNovelty}.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ItemNoveltyReranker<U, I> extends PermutationReranker<U, I> {

    private final double lambda;
    private final ItemNovelty<U, I> novelty;
    private final boolean norm;

    /**
     * Constructor.
     *
     * @param lambda trade-off between relevance and novelty
     * @param novelty item novelty model
     * @param norm normalize the relevance and novelty scores
     */
    public ItemNoveltyReranker(double lambda, ItemNovelty<U, I> novelty, boolean norm) {
        this.lambda = lambda;
        this.novelty = novelty;
        this.norm = norm;
    }

    @Override
    public int[] rerankPermutation(Recommendation<U, I> recommendation, int maxLength) {
        U user = recommendation.getUser();
        List<Tuple2od<I>> items = recommendation.getItems();
        int M = items.size();
        int N = min(maxLength, M);
        
        if (lambda == 0.0) {
            return getBasePerm(N);
        }

        ItemNovelty.UserItemNoveltyModel<U, I> uinm = novelty.getModel(user);
        if (uinm == null) {
            return new int[0];
        }

        Object2DoubleMap<I> novMap = new Object2DoubleOpenHashMap<>();
        Stats relStats = new Stats();
        Stats novStats = new Stats();
        recommendation.getItems().forEach(itemValue -> {
            double nov = uinm.novelty(itemValue.v1);
            novMap.put(itemValue.v1, nov);
            relStats.accept(itemValue.v2);
            novStats.accept(nov);
        });

        IntDoubleTopN topN = new IntDoubleTopN(N);
        for (int i = 0; i < M; i++) {
            topN.add(M - i, value(items.get(i), relStats, novMap, novStats));
        }
        topN.sort();

        int[] perm = topN.reverseStream()
                .mapToInt(e -> M - e.v1)
                .toArray();

        return perm;
    }

    /**
     * Returns the normalized value of a relevance or novelty score.
     *
     * @param score the relevance or novelty score
     * @param stats the relevance or novelty statistics
     * @return the normalized score
     */
    protected double norm(double score, Stats stats) {
        if (norm) {
            return (score - stats.getMean()) / stats.getStandardDeviation();
        } else {
            return score;
        }
    }

    /**
     * Re-scored value to determine the re-ranking.
     *
     * @param iv item-relevance pair from the input recommendation
     * @param relStats statistics about the relevance scores
     * @param novMap item-novelty pairs
     * @param novStats statistics about the novelty scores 
     * @return the new score resulting by a normalized linear combination 
     * between relevance and novelty
     */
    protected double value(Tuple2od<I> iv, Stats relStats, Object2DoubleMap<I> novMap, Stats novStats) {
        return (1 - lambda) * norm(iv.v2, relStats) + lambda * norm(novMap.getDouble(iv.v1), novStats);
    }
}
