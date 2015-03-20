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
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
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
public abstract class SetSimilarity implements Similarity {

    private final FastPreferenceData<?, ?, ?> data;

    public SetSimilarity(FastPreferenceData<?, ?, ?> data) {
        this.data = data;
    }

    @Override
    public IntToDoubleFunction similarity(int idx1) {
        IntSet set = new IntOpenHashSet();
        data.getUidxPreferences(idx1).map(iv -> iv.idx).forEach(set::add);

        return idx2 -> {
            int coo = (int) data.getUidxPreferences(idx2)
                    .map(iv -> iv.idx)
                    .filter(set::contains)
                    .count();

            return sim(coo, set.size(), data.numItems(idx2));
        };
    }

    private Int2IntMap getIntersectionMap(int aidx) {
        Int2IntOpenHashMap intersectionMap = new Int2IntOpenHashMap();
        intersectionMap.defaultReturnValue(0);

        data.getUidxPreferences(aidx).forEach(ip -> {
            data.getIidxPreferences(ip.idx).forEach(up -> {
                intersectionMap.addTo(up.idx, 1);
            });
        });

        intersectionMap.remove(aidx);

        return intersectionMap;
    }

    @Override
    public Stream<IdxDouble> similarElems(int idx1) {
        Int2IntMap intersectionMap = getIntersectionMap(idx1);

        final int na = data.numItems(idx1);

        Builder<IdxDouble> builder = builder();
        intersectionMap.int2IntEntrySet().forEach(e -> {
            int idx2 = e.getIntKey();
            int coo = e.getIntValue();
            builder.accept(new IdxDouble(idx2, sim(coo, na, data.numItems(idx2))));
        });

        return builder.build();
    }

    protected abstract double sim(int intersectionSize, int na, int nb);
}
