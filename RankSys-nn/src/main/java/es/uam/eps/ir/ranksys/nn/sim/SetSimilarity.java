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
import es.uam.eps.ir.ranksys.fast.preference.FasterPreferenceData;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.IntToDoubleFunction;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;

/**
 * Set similarity. Based on the intersection of item/user profiles as sets.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public abstract class SetSimilarity implements Similarity {

    protected final FastPreferenceData<?, ?> data;
    protected final boolean dense;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param dense true for array-based calculations, false to map-based
     */
    public SetSimilarity(FastPreferenceData<?, ?> data, boolean dense) {
        this.data = data;
        this.dense = dense;
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

    protected Int2IntMap getIntersectionMap(int idx1) {
        Int2IntOpenHashMap intersectionMap = new Int2IntOpenHashMap();
        intersectionMap.defaultReturnValue(0);

        data.getUidxPreferences(idx1).forEach(ip -> {
            data.getIidxPreferences(ip.idx).forEach(up -> {
                intersectionMap.addTo(up.idx, 1);
            });
        });

        intersectionMap.remove(idx1);

        return intersectionMap;
    }

    protected int[] getIntersectionArray(int idx1) {
        int[] intersectionMap = new int[data.numUsers()];

        data.getUidxPreferences(idx1).forEach(ip -> {
            data.getIidxPreferences(ip.idx).forEach(up -> {
                intersectionMap[up.idx]++;
            });
        });

        intersectionMap[idx1] = 0;

        return intersectionMap;
    }

    protected Int2IntMap getFasterIntersectionMap(int uidx) {
        Int2IntOpenHashMap intersectionMap = new Int2IntOpenHashMap();
        intersectionMap.defaultReturnValue(0);

        IntIterator iidxs = ((FasterPreferenceData<?, ?>) data).getUidxIidxs(uidx);
        while (iidxs.hasNext()) {
            IntIterator vidxs = ((FasterPreferenceData<?, ?>) data).getIidxUidxs(iidxs.nextInt());
            while (vidxs.hasNext()) {
                intersectionMap.addTo(vidxs.nextInt(), 1);
            }
        }

        intersectionMap.remove(uidx);

        return intersectionMap;
    }

    protected int[] getFasterIntersectionArray(int uidx) {
        int[] intersectionMap = new int[data.numUsers()];

        IntIterator iidxs = ((FasterPreferenceData<?, ?>) data).getUidxIidxs(uidx);
        while (iidxs.hasNext()) {
            IntIterator vidxs = ((FasterPreferenceData<?, ?>) data).getIidxUidxs(iidxs.nextInt());
            while (vidxs.hasNext()) {
                intersectionMap[vidxs.nextInt()]++;
            }
        }

        intersectionMap[uidx] = 0;

        return intersectionMap;
    }

    @Override
    public Stream<IdxDouble> similarElems(int idx1) {
        int na = data.numItems(idx1);

        if (data instanceof FasterPreferenceData) {
            if (dense) {
                int[] intersectionMap = getFasterIntersectionArray(idx1);
                return range(0, intersectionMap.length)
                        .filter(i -> intersectionMap[i] != 0)
                        .mapToObj(i -> new IdxDouble(i, sim(intersectionMap[i], na, data.numItems(i))));
            } else {
                return getFasterIntersectionMap(idx1).int2IntEntrySet().stream()
                        .map(e -> {
                            int idx2 = e.getIntKey();
                            int coo = e.getIntValue();
                            int nb = data.numItems(idx2);
                            return new IdxDouble(idx2, sim(coo, na, nb));
                        });
            }
        } else {
            if (dense) {
                int[] intersectionMap = getIntersectionArray(idx1);
                return range(0, intersectionMap.length)
                        .filter(i -> intersectionMap[i] != 0)
                        .mapToObj(i -> new IdxDouble(i, sim(intersectionMap[i], na, data.numItems(i))));
            } else {
                return getIntersectionMap(idx1).int2IntEntrySet().stream()
                        .map(e -> {
                            int idx2 = e.getIntKey();
                            int coo = e.getIntValue();
                            int nb = data.numItems(idx2);
                            return new IdxDouble(idx2, sim(coo, na, nb));
                        });
            }
        }
    }

    /**
     * Calculates the similarity value.
     *
     * @param intersectionSize size of the intersection of sets
     * @param na size of the first set
     * @param nb size of the second set
     * @return similarity value
     */
    protected abstract double sim(int intersectionSize, int na, int nb);
}
