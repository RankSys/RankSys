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
package es.uam.eps.ir.ranksys.nn.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import static java.lang.Math.pow;

/**
 * Set cosine similarity. As in Aiolli's paper. Can be asymmetric if 
 * alpha != 0.5.
 * 
 * sim(A, B) = |A n B| / (|A|^alpha |B|^(1 - alpha))
 *
 * F. Aiolli. Efficient Top-N Recommendation for Very Large Scale Binary Rated
 * Datasets. RecSys 2013.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SetCosineSimilarity extends SetSimilarity {

    private final double alpha;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param alpha asymmetry of the similarity, set to 0.5 for standard cosine
     */
    public SetCosineSimilarity(FastPreferenceData<?, ?, ?> data, double alpha, boolean fast) {
        super(data, fast);
        this.alpha = alpha;
    }

    @Override
    protected double sim(int intersectionSize, int nA, int nB) {
        return intersectionSize / (pow(nA, alpha) * pow(nB, 1.0 - alpha));
    }
}
