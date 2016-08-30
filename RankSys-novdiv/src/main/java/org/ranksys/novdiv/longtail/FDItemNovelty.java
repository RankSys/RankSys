/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.novdiv.longtail;

import org.ranksys.novdiv.itemnovelty.ItemNovelty;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import static java.lang.Math.log;
import java.util.IntSummaryStatistics;
import org.ranksys.core.preference.PreferenceData;

/**
 * Free discovery item novelty model.
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
public class FDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final UserFDItemNoveltyModel nov;

    /**
     * Constructor
     *
     * @param recommenderData preference data
     */
    public FDItemNovelty(PreferenceData<U, I> recommenderData) {
        super();
        this.nov = new UserFDItemNoveltyModel(recommenderData);
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U t) {
        return nov;
    }

    @Override
    public UserItemNoveltyModel<U, I> getModel(U u) {
        return nov;
    }

    private class UserFDItemNoveltyModel implements UserItemNoveltyModel<U, I> {

        private final Object2DoubleMap<I> itemNovelty;

        public UserFDItemNoveltyModel(PreferenceData<U, I> recommenderData) {
            IntSummaryStatistics stats = recommenderData.getItemsWithPreferences().mapToInt(i -> recommenderData.numUsers(i)).summaryStatistics();
            double norm = stats.getSum();
            double maxNov = -log(stats.getMin() / norm) / log(2);

            itemNovelty = new Object2DoubleOpenHashMap<>();
            itemNovelty.defaultReturnValue(maxNov);
            recommenderData.getItemsWithPreferences().forEach(i -> {
                itemNovelty.put(i, -log(recommenderData.numUsers(i) / norm) / log(2));
            });
        }

        @Override
        public double novelty(I i) {
            return itemNovelty.getDouble(i);
        }

    }
}
