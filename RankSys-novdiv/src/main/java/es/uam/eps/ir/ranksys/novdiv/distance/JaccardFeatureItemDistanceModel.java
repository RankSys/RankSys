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
package es.uam.eps.ir.ranksys.novdiv.distance;

import es.uam.eps.ir.ranksys.core.feature.FeatureData;

/**
 * Vector Jaccard item distance model.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 * @param <F> type of the features
 */
public class JaccardFeatureItemDistanceModel<I, F> extends VectorFeatureItemDistanceModel<I, F> {

    /**
     * Constructor.
     *
     * @param featureData feature data
     */
    public JaccardFeatureItemDistanceModel(FeatureData<I, F, Double> featureData) {
        super(featureData);
    }

    @Override
    protected double dist(double prod, double norm2A, double norm2B) {
        return 1 - prod / (norm2A + norm2B - prod);
    }

}
