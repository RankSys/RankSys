/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.Similarities;

/**
 * Static methods for constructing item similarities.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class ItemSimilarities {

    /**
     * Set cosine item similarity.
     *
     * @param preferences preference data
     * @param alpha       asymmetry factor, set to 0.5 to standard cosine.
     * @param dense       true for array-based calculations, false to map-based
     * @param <I>         item type
     * @return item similarity
     */
    public static <I> ItemSimilarity<I> setCosine(FastPreferenceData<?, I> preferences, double alpha, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.setCosine(new TransposedPreferenceData<>(preferences), dense, alpha));
    }

    /**
     * Vector cosine item similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <I>         item type
     * @return item similarity
     */
    public static <I> ItemSimilarity<I> vectorCosine(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.vectorCosine(new TransposedPreferenceData<>(preferences), dense));
    }

    /**
     * Set Jaccard item similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <I>         item type
     * @return item similarity
     */
    public static <I> ItemSimilarity<I> setJaccard(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.setJaccard(new TransposedPreferenceData<>(preferences), dense));
    }

    /**
     * Vector Jaccard item similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <I>         item type
     * @return item similarity
     */
    public static <I> ItemSimilarity<I> vectorJaccard(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.vectorJaccard(new TransposedPreferenceData<>(preferences), dense));
    }

    /**
     * Log likelihood item similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <I>         item type
     * @return item similarity
     */
    public static <I> ItemSimilarity<I> logLikelihood(FastPreferenceData<?, I> preferences, boolean dense) {
        return new ItemSimilarity<>(preferences, Similarities.logLikelihood(new TransposedPreferenceData<>(preferences), dense));
    }

}
