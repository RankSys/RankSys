/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
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
package es.uam.eps.ir.ranksys.fast.data;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.DoubleParser;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.fast.IdxPref;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class SimpleFastRecommenderData<U, I, O> extends AbstractFastRecommenderData<U, I, O> {

    private final int numPreferences;
    private final List<List<IdxPref<O>>> uidxList;
    private final List<List<IdxPref<O>>> iidxList;

    protected SimpleFastRecommenderData(int numPreferences, List<List<IdxPref<O>>> uidxList, List<List<IdxPref<O>>> iidxList, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);
        this.numPreferences = numPreferences;
        this.uidxList = uidxList;
        this.iidxList = iidxList;
    }

    @Override
    public int numUsers(int iidx) {
        return iidxList.get(iidx).size();
    }

    @Override
    public int numItems(int uidx) {
        return uidxList.get(uidx).size();
    }

    @Override
    public Stream<IdxPref<O>> getUidxPreferences(int uidx) {
        return uidxList.get(uidx).stream();
    }

    @Override
    public Stream<IdxPref<O>> getIidxPreferences(int iidx) {
        return iidxList.get(iidx).stream();
    }

    @Override
    public int numPreferences() {
        return numPreferences;
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return IntStream.range(0, numUsers())
                .filter(uidx -> uidxList.get(uidx) != null);
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return IntStream.range(0, numItems())
                .filter(iidx -> iidxList.get(iidx) != null);
    }

    @Override
    public int numUsersWithPreferences() {
        return (int) uidxList.stream()
                .filter(iv -> iv != null).count();
    }

    @Override
    public int numItemsWithPreferences() {
        return (int) iidxList.stream()
                .filter(iv -> iv != null).count();
    }

    public static <U, I, O> SimpleFastRecommenderData<U, I, O> load(String path, Parser<U> uParser, Parser<I> iParser, DoubleParser dp, Parser<O> vParser, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) throws IOException {
        return load(new FileInputStream(path), uParser, iParser, dp, vParser, uIndex, iIndex);
    }

    public static <U, I, O> SimpleFastRecommenderData<U, I, O> load(InputStream in, Parser<U> uParser, Parser<I> iParser, DoubleParser dp, Parser<O> vParser, FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) throws IOException {
        int[] numPreferences = new int[]{0};
        
        List<List<IdxPref<O>>> uidxList = new ArrayList<>();
        for (int uidx = 0; uidx < uIndex.numUsers(); uidx++) {
            uidxList.add(null);
        }
        
        List<List<IdxPref<O>>> iidxList = new ArrayList<>();
        for (int iidx = 0; iidx < iIndex.numItems(); iidx++) {
            iidxList.add(null);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(l -> {
                CharSequence[] tokens = split(l, '\t', 4);
                U user = uParser.parse(tokens[0]);
                I item = iParser.parse(tokens[1]);
                double value;
                if (tokens.length >= 3) {
                    value = dp.parse(tokens[2]);
                } else {
                    value = dp.parse(null);
                }
                O other;
                if (tokens.length == 4) {
                    other = vParser.parse(tokens[3]);
                } else {
                    other = vParser.parse(null);
                }

                int uidx = uIndex.user2uidx(user);
                int iidx = iIndex.item2iidx(item);

                numPreferences[0]++;

                List<IdxPref<O>> uList = uidxList.get(uidx);
                if (uList == null) {
                    uList = new ArrayList<>();
                    uidxList.set(uidx, uList);
                }
                uList.add(new IdxPref<>(iidx, value, other));

                List<IdxPref<O>> iList = iidxList.get(iidx);
                if (iList == null) {
                    iList = new ArrayList<>();
                    iidxList.set(iidx, iList);
                }
                iList.add(new IdxPref<>(uidx, value, other));
            });
        }

        return new SimpleFastRecommenderData<>(numPreferences[0], uidxList, iidxList, uIndex, iIndex);
    }

}
