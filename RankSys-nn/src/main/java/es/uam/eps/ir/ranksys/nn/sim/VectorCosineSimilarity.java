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
 * Vector cosine similarity. As in Cremonesi's paper. Can be asymmetric if 
 * alpha != 0.5.
 * 
 * sim(v, w) = v * w / ((v * v)^alpha (w * w)^(1 - alpha))
 *
 * F. Aiolli. Efficient Top-N Recommendation for Very Large Scale Binary Rated
 * Datasets. RecSys 2013.
 * 
 * P. Cremonesi, Y. Koren, and R. Turrin. Performance of 
 * recommender algorithms on top-N recommendation tasks. RecSys 2010.
 * 
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class VectorCosineSimilarity extends VectorSimilarity {

    private final double alpha;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param alpha asymmetry of the similarity, set to 0.5 for symmetry
     */
    public VectorCosineSimilarity(FastPreferenceData<?, ?, ?> data, double alpha, boolean fast) {
        super(data, fast);
        this.alpha = alpha;
    }

    @Override
    protected double sim(double product, double norm2A, double norm2B) {
        return product / (pow(norm2A, alpha) * pow(norm2B, 1.0 - alpha));
    }

}
