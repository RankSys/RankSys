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
package es.uam.eps.ir.ranksys.fast.index;

import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import es.uam.eps.ir.ranksys.fast.utils.IdxIndex;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

/**
 * Simple implementation of FastFeatureIndex backed by a bi-map IdxIndex
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <F> type of the features
 */
public class SimpleFastFeatureIndex<F> implements FastFeatureIndex<F> {

    private final IdxIndex<F> fMap;

    /**
     * Constructor.
     *
     */
    protected SimpleFastFeatureIndex() {
        this.fMap = new IdxIndex<>();
    }

    @Override
    public boolean containsFeature(F f) {
        return fMap.containsId(f);
    }

    @Override
    public int numFeatures() {
        return fMap.size();
    }

    @Override
    public Stream<F> getAllFeatures() {
        return fMap.getIds();
    }

    @Override
    public int feature2fidx(F f) {
        return fMap.get(f);
    }

    @Override
    public F fidx2feature(int fidx) {
        return fMap.get(fidx);
    }

    /**
     * Add a new feature to the index. If the feature already exists, nothing is done.
     *
     * @param f id of the feature
     * @return index of the feature
     */
    protected int add(F f) {
        return fMap.add(f);
    }

    /**
     * Creates a feature index from a file where the first column lists the features.
     *
     * @param <F> type of the features
     * @param path path of the file
     * @param fParser feature type parser
     * @return a fast feature index
     * @throws IOException when file does not exist or when IO error
     */
    public static <F> SimpleFastFeatureIndex<F> load(String path, Parser<F> fParser) throws IOException {
        return load(new FileInputStream(path), fParser);
    }

    /**
     * Creates a feature index from an input stream where the first column lists the features.
     *
     * @param <F> type of the features
     * @param in input stream
     * @param iParser feature type parser
     * @return a fast feature index
     * @throws IOException when IO error
     */
    public static <F> SimpleFastFeatureIndex<F> load(InputStream in, Parser<F> iParser) throws IOException {
        SimpleFastFeatureIndex<F> featureIndex = new SimpleFastFeatureIndex<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines()
                    .map(line -> iParser.parse(split(line, '\t')[0]))
                    .sorted()
                    .forEach(f -> featureIndex.add(f));
        }
        return featureIndex;
    }

}
