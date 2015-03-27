/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.mf;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;

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
    public abstract double error(Factorization<U, I> factorization, FastPreferenceData<U, I, ?> data);

    /**
     * Creates and calculates a factorization.
     *
     * @param K size of the latent feature space.
     * @param data preference data
     * @return a matrix factorization
     */
    public abstract Factorization<U, I> factorize(int K, FastPreferenceData<U, I, ?> data);

    /**
     * Calculates the factorization by using a previously generate matrix 
     * factorization.
     *
     * @param factorization matrix factorization
     * @param data preference data
     */
    public abstract void factorize(Factorization<U, I> factorization, FastPreferenceData<U, I, ?> data);
}
