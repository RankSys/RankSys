/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.mf;

import org.ranksys.core.preference.fast.FastPreferenceData;

/**
 * Factorizer. Abstract class for matrix factorization algorithms.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public abstract class Factorizer<U, I> {

    /**
     * Global loss of the factorization.
     *
     * @param factorization matrix factorization
     * @param data preference data
     * @return the global loss
     */
    public abstract double error(Factorization<U, I> factorization, FastPreferenceData<U, I> data);

    /**
     * Creates and calculates a factorization.
     *
     * @param K size of the latent feature space.
     * @param data preference data
     * @return a matrix factorization
     */
    public abstract Factorization<U, I> factorize(int K, FastPreferenceData<U, I> data);

    /**
     * Calculates the factorization by using a previously generate matrix 
     * factorization.
     *
     * @param factorization matrix factorization
     * @param data preference data
     */
    public abstract void factorize(Factorization<U, I> factorization, FastPreferenceData<U, I> data);
}
