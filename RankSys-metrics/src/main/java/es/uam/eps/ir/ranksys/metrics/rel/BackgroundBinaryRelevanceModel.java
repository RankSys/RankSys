/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.metrics.rel;

import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import es.uam.eps.ir.ranksys.metrics.rel.RelevanceModel.UserRelevanceModel;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

/**
 * Background discount model: assumes the relevance of unseen items in a test
 * subset with a pre-fixed gain value.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class BackgroundBinaryRelevanceModel<U, I> extends RelevanceModel<U, I> {

    private final PreferenceData<U, I> testData;
    private final double threshold;
    private final double background;

    /**
     * Constructor.
     *
     * @param caching are the user relevance models being cached?
     * @param testData test subset of preferences
     * @param threshold relevance threshold
     * @param background gain of unseen items in the test subset
     */
    public BackgroundBinaryRelevanceModel(boolean caching, PreferenceData<U, I> testData, double threshold, double background) {
        super(caching, testData.getUsersWithPreferences());
        this.testData = testData;
        this.threshold = threshold;
        this.background = background;
    }

    @Override
    protected UserRelevanceModel<U, I> get(U user) {
        return new UserSmo4RelevanceModel(user);
    }

    private class UserSmo4RelevanceModel implements UserRelevanceModel<U, I> {

        private final Object2DoubleMap<I> gainMap;

        public UserSmo4RelevanceModel(U user) {
            this.gainMap = new Object2DoubleOpenHashMap<>();
            this.gainMap.defaultReturnValue(background);

            testData.getUserPreferences(user).forEach(iv -> {
                if (iv.v2 >= threshold) {
                    gainMap.put(iv.v1, 1.0);
                } else {
                    gainMap.put(iv.v1, 0.0);
                }
            });
        }

        @Override
        public boolean isRelevant(I item) {
            return gain(item) > 0.0;
        }

        @Override
        public double gain(I item) {
            return gainMap.getDouble(item);
        }

    }
}
