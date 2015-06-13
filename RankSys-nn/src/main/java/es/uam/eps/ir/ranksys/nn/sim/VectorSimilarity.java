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

import es.uam.eps.ir.ranksys.fast.IdxDouble;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;

/**
 * Vector similarity. Based on the inner product of item/user profiles as vectors.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class VectorSimilarity implements Similarity {

    protected final FastPreferenceData<?, ?, ?> data;
    protected final Int2DoubleMap norm2Map;

    /**
     * Constructor.
     *
     * @param data preference data
     */
    public VectorSimilarity(FastPreferenceData<?, ?, ?> data) {
        this.data = data;
        this.norm2Map = new Int2DoubleOpenHashMap();
        norm2Map.defaultReturnValue(0.0);
        data.getUidxWithPreferences().forEach(idx -> norm2Map.put(idx, getNorm2(idx)));
    }

    @Override
    public IntToDoubleFunction similarity(int idx1) {
        Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();
        data.getUidxPreferences(idx1).forEach(iv -> map.put(iv.idx, iv.v));

        double n2a = norm2Map.get(idx1);

        return idx2 -> {
            double prod = data.getUidxPreferences(idx2)
                    .mapToDouble(iv -> iv.v * map.get(iv.idx))
                    .sum();

            return sim(prod, n2a, norm2Map.get(idx2));
        };
    }

    protected Int2DoubleMap getProductMap(int idx1) {
        Int2DoubleOpenHashMap productMap = new Int2DoubleOpenHashMap();
        productMap.defaultReturnValue(0.0);

        data.getUidxPreferences(idx1).forEach(ip -> {
            data.getIidxPreferences(ip.idx).forEach(up -> {
                productMap.addTo(up.idx, ip.v * up.v);
            });
        });

        productMap.remove(idx1);

        return productMap;
    }

    protected double getNorm2(int idx) {
        return data.getUidxPreferences(idx).mapToDouble(ip -> ip.v * ip.v).sum();
    }

    @Override
    public Stream<IdxDouble> similarElems(int idx1) {
        double n2a = norm2Map.get(idx1);

        return getProductMap(idx1).int2DoubleEntrySet().stream()
                .map(e -> {
                    int idx2 = e.getIntKey();
                    double coo = e.getDoubleValue();
                    double n2b = norm2Map.get(idx2);
                    return new IdxDouble(idx2, sim(coo, n2a, n2b));
                });
    }

    /**
     * Calculates the similarity value.
     *
     * @param product value of the inner product of vectors
     * @param norm2A square of the norm of the first vector
     * @param norm2B square of the norm of the second vector
     * @return similarity value
     */
    protected abstract double sim(double product, double norm2A, double norm2B);
}
