/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novdiv.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import es.uam.eps.ir.ranksys.rec.fast.FastRecommender;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Wrapper for re-ranker that re-ranks the output of another recommender.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class RerankingRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final FastRecommender<U, I> recommender;
    private final Reranker<U, I> reranker;

    /**
     * Constructor.
     *
     * @param uIndex user index
     * @param iIndex item index
     * @param recommender input recommender
     * @param reranker re-ranker to apply to input recommender
     */
    public RerankingRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex, FastRecommender<U, I> recommender, Reranker<U, I> reranker) {
        super(uIndex, iIndex);
        this.recommender = recommender;
        this.reranker = reranker;
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, int maxLength, Predicate<I> filter) {
        return reranker.rerankRecommendation(recommender.getRecommendation(u, 0, filter), maxLength);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        FastRecommendation frec = recommender.getRecommendation(uidx, 0, filter);
        
        U user = uidx2user(uidx);
        List<IdDouble<I>> items = frec.getIidxs().stream()
                .map(iv -> new IdDouble<>(iidx2item(iv.idx), iv.v))
                .collect(Collectors.toList());
        Recommendation<U, I> rec = new Recommendation<>(user, items);
        
        rec = reranker.rerankRecommendation(rec, maxLength);
        
        List<IdxDouble> iidxs = rec.getItems().stream()
                .map(iv -> new IdxDouble(item2iidx(iv.id), iv.v))
                .collect(Collectors.toList());
        frec = new FastRecommendation(uidx, iidxs);
        
        return frec;
    }
}
