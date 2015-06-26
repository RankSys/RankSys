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
package es.uam.eps.ir.ranksys.nn.item.sim;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.TransposedPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.SetCosineSimilarity;

/**
 * Set cosine similarity. See {@link SetCosineSimilarity}.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <I> type of the items
 */
public class SetCosineItemSimilarity<I> extends ItemSimilarity<I> {

    /**
     * Constructor.
     *
     * @param data preference
     * @param alpha asymmetry of the similarity, set to 0.5 to standard cosine.
     */
    public SetCosineItemSimilarity(FastPreferenceData<?, I, ?> data, double alpha, boolean fast) {
        super(data, new SetCosineSimilarity(new TransposedPreferenceData<>(data), alpha, fast));
    }

}
