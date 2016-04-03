/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.unexp;

import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.novdiv.distance.ItemDistanceModel;
import java.util.function.ToDoubleFunction;
import org.ranksys.core.util.tuples.Tuple2od;

/**
 * Expected profile distance item novelty.
 *
 * S. Vargas. Novelty and diversity evaluation and enhancement in Recommender
 * Systems. PhD Thesis.
 * 
 * S. Vargas and P. Castells. Rank and relevance in novelty and diversity for
 * Recommender Systems. RecSys 2011.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class PDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final PreferenceData<U, I> recommenderData;
    private final ItemDistanceModel<I> dist;

    /**
     * Constructor.
     *
     * @param caching are profile distances cached?
     * @param recommenderData preference data
     * @param dist item distance model
     */
    public PDItemNovelty(boolean caching, PreferenceData<U, I> recommenderData, ItemDistanceModel<I> dist) {
        super(caching, recommenderData.getUsersWithPreferences());
        this.recommenderData = recommenderData;
        this.dist = dist;
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U u) {
        return new UserPDItemNovelty(u);
    }

    private class UserPDItemNovelty implements UserItemNoveltyModel<U, I> {

        private final U u;

        public UserPDItemNovelty(U u) {
            this.u = u;
        }

        @Override
        public double novelty(I i) {
            ToDoubleFunction<I> iDist = dist.dist(i);
            return recommenderData.getUserPreferences(u)
                    .map(Tuple2od::v1)
                    .mapToDouble(j -> iDist.applyAsDouble(j))
                    .filter(v -> !Double.isNaN(v))
                    .average().orElse(0.0);
        }

    }
}
