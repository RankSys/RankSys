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
package es.uam.eps.ir.ranksys.fast.feature;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.fast.IdxVar;
import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
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
public class SimpleFastFeatureData<I, F, V> extends AbstractFastFeatureData<I, F, V> {

    private final List<List<IdxVar<V>>> iidxList;
    private final List<List<IdxVar<V>>> fidxList;

    protected SimpleFastFeatureData(List<List<IdxVar<V>>> iidxList, List<List<IdxVar<V>>> fidxList, FastItemIndex<I> ii, FastFeatureIndex<F> fi) {
        super(ii, fi);
        this.iidxList = iidxList;
        this.fidxList = fidxList;
    }

    @Override
    public Stream<IdxVar<V>> getIidxFeatures(int iidx) {
        return iidxList.get(iidx).stream();
    }

    @Override
    public Stream<IdxVar<V>> getFidxItems(int fidx) {
        return fidxList.get(fidx).stream();
    }

    @Override
    public int numItems(int fidx) {
        return fidxList.get(fidx).size();
    }

    @Override
    public int numFeatures(int iidx) {
        return iidxList.get(iidx).size();
    }

    @Override
    public IntStream getIidxWithFeatures() {
        return IntStream.range(0, numItems())
                .filter(iidx -> iidxList.get(iidx) != null);
    }

    @Override
    public IntStream getFidxWithItems() {
        return IntStream.range(0, numFeatures())
                .filter(fidx -> fidxList.get(fidx) != null);
    }

    @Override
    public int numItemsWithFeatures() {
        return (int) iidxList.stream()
                .filter(iv -> iv != null).count();
    }

    @Override
    public int numFeaturesWithItems() {
        return (int) fidxList.stream()
                .filter(fv -> fv != null).count();
    }
    
        public static <I, F, V> SimpleFastFeatureData<I, F, V> load(String path, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser, FastItemIndex<I> iIndex, FastFeatureIndex<F> fIndex) throws IOException {
        return load(new FileInputStream(path), iParser, fParser, vParser, iIndex, fIndex);
    }

    public static <I, F, V> SimpleFastFeatureData<I, F, V> load(InputStream in, Parser<I> iParser, Parser<F> fParser, Parser<V> vParser, FastItemIndex<I> iIndex, FastFeatureIndex<F> fIndex) throws IOException {

        List<List<IdxVar<V>>> iidxList = new ArrayList<>();
        for (int iidx=  0; iidx < iIndex.numItems(); iidx++) {
            iidxList.add(null);
        }
        
        List<List<IdxVar<V>>> fidxList = new ArrayList<>();
        for (int fidx = 0; fidx < fIndex.numFeatures(); fidx++) {
            fidxList.add(null);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(l -> {
                CharSequence[] tokens = split(l, '\t', 3);
                I item = iParser.parse(tokens[0]);
                F feature = fParser.parse(tokens[1]);
                V value;
                if (tokens.length == 3) {
                    value = vParser.parse(tokens[2]);
                } else {
                    value = vParser.parse(null);
                }

                int iidx = iIndex.item2iidx(item);
                int fidx = fIndex.feature2fidx(feature);

                List<IdxVar<V>> iList = iidxList.get(iidx);
                if (iList == null) {
                    iList = new ArrayList<>();
                    iidxList.set(iidx, iList);
                }
                iList.add(new IdxVar<>(fidx, value));

                List<IdxVar<V>> fList = fidxList.get(fidx);
                if (fList == null) {
                    fList = new ArrayList<>();
                    fidxList.set(fidx, fList);
                }
                fList.add(new IdxVar<>(iidx, value));
            });
        }

        return new SimpleFastFeatureData<>(iidxList, fidxList, iIndex, fIndex);
    }

}
