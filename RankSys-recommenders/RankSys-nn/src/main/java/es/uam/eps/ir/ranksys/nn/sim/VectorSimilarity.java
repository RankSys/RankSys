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
import es.uam.eps.ir.ranksys.fast.data.FastRecommenderData;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import static java.util.stream.Stream.builder;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class VectorSimilarity implements Similarity {

    private final FastRecommenderData<?, ?, ?> data;
    private final Int2DoubleMap norm2Map;

    public VectorSimilarity(FastRecommenderData<?, ?, ?> data) {
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

    private Int2DoubleMap getProductMap(int idx) {
        Int2DoubleOpenHashMap productMap = new Int2DoubleOpenHashMap();
        productMap.defaultReturnValue(0.0);

        data.getUidxPreferences(idx).forEach(ip -> {
            data.getIidxPreferences(ip.idx).forEach(up -> {
                productMap.addTo(up.idx, ip.v * up.v);
            });
        });

        productMap.remove(idx);

        return productMap;
    }

    private double getNorm2(int idx) {
        return data.getUidxPreferences(idx).mapToDouble(ip -> ip.v * ip.v).sum();
    }

    @Override
    public Stream<IdxDouble> similarElems(int idx1) {
        Int2DoubleMap productMap = getProductMap(idx1);

        double n2a = norm2Map.get(idx1);

        Builder<IdxDouble> builder = builder();
        productMap.int2DoubleEntrySet().forEach(e -> {
            int idx2 = e.getIntKey();
            double coo = e.getDoubleValue();
            builder.accept(new IdxDouble(idx2, sim(coo, n2a, norm2Map.get(idx2))));
        });

        return builder.build();
    }

    protected abstract double sim(double product, double norm2A, double norm2B);
}
