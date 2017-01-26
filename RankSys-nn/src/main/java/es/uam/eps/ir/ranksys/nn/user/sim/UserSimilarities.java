/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.nn.user.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.Similarities;

/**
 * Static methods for constructing user similarities.
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
public class UserSimilarities {

    /**
     * Set cosine user similarity.
     *
     * @param preferences preference data
     * @param alpha       asymmetry factor, set to 0.5 to standard cosine.
     * @param dense       true for array-based calculations, false to map-based
     * @param <U>         user type
     * @return user similarity
     */
    public static <U> UserSimilarity<U> setCosine(FastPreferenceData<U, ?> preferences, double alpha, boolean dense) {
        return new UserSimilarity<>(preferences, Similarities.setCosine(preferences, dense, alpha));
    }

    /**
     * Vector cosine user similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <U>         user type
     * @return user similarity
     */
    public static <U> UserSimilarity<U> vectorCosine(FastPreferenceData<U, ?> preferences, boolean dense) {
        return new UserSimilarity<>(preferences, Similarities.vectorCosine(preferences, dense));
    }

    /**
     * Set Jaccard user similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <U>         user type
     * @return user similarity
     */
    public static <U> UserSimilarity<U> setJaccard(FastPreferenceData<U, ?> preferences, boolean dense) {
        return new UserSimilarity<>(preferences, Similarities.setJaccard(preferences, dense));
    }

    /**
     * Vector Jaccard user similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <U>         user type
     * @return user similarity
     */
    public static <U> UserSimilarity<U> vectorJaccard(FastPreferenceData<U, ?> preferences, boolean dense) {
        return new UserSimilarity<>(preferences, Similarities.vectorJaccard(preferences, dense));
    }

    /**
     * Log likelihood user similarity.
     *
     * @param preferences preference data
     * @param dense       true for array-based calculations, false to map-based
     * @param <U>         user type
     * @return user similarity
     */
    public static <U> UserSimilarity<U> logLikelihood(FastPreferenceData<U, ?> preferences, boolean dense) {
        return new UserSimilarity<>(preferences, Similarities.logLikelihood(preferences, dense));
    }

}
