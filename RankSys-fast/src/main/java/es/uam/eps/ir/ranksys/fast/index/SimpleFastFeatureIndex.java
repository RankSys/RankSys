/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
