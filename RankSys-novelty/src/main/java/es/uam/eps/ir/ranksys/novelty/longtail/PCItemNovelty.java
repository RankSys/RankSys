/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.novelty.longtail;

import es.uam.eps.ir.ranksys.novdiv.itemnovelty.ItemNovelty;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

/**
 * Popularity complement item novelty model.
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
public class PCItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final UserPCItemNoveltyModel nov;

    /**
     * Constructor
     *
     * @param recommenderData preference data
     */
    public PCItemNovelty(PreferenceData<U, I> recommenderData) {
        super();
        this.nov = new UserPCItemNoveltyModel(recommenderData);
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U t) {
        return nov;
    }

    @Override
    public UserItemNoveltyModel<U, I> getModel(U u) {
        return nov;
    }

    private class UserPCItemNoveltyModel implements UserItemNoveltyModel<U, I> {

        private final Object2DoubleMap<I> itemNovelty;

        public UserPCItemNoveltyModel(PreferenceData<U, I> recommenderData) {
            itemNovelty = new Object2DoubleOpenHashMap<>();
            itemNovelty.defaultReturnValue(1.0);
            int numUsers = recommenderData.numUsersWithPreferences();
            recommenderData.getItemsWithPreferences().forEach(i -> {
                itemNovelty.put(i, 1 - recommenderData.numUsers(i) / (double) numUsers);
            });
        }

        @Override
        public double novelty(I i) {
            return itemNovelty.getDouble(i);
        }

    }
}
