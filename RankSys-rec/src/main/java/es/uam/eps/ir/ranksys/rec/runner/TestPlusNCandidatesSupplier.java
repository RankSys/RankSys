/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.rec.runner;

import es.uam.eps.ir.ranksys.core.IdObject;
import es.uam.eps.ir.ranksys.core.preference.PreferenceData;
import static es.uam.eps.ir.ranksys.core.util.FastStringSplitter.split;
import es.uam.eps.ir.ranksys.core.util.parsing.Parser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Provider of candidates for recommendation consisting in the preferences in a
 * test set plus a set of randomly selected items from a test file for each user.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * 
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class TestPlusNCandidatesSupplier<U, I> implements Supplier<Stream<IdObject<U, List<I>>>> {

    private final PreferenceData<U, I> testData;
    private final Parser<U> uParser;
    private final Parser<I> iParser;
    private final String candidatesPath;

    /**
     * Constructor.
     *
     * @param testData test preference data
     * @param uParser user type parser
     * @param iParser item type parser
     * @param candidatesPath path to the file of random items to add to the
     * candidates
     */
    public TestPlusNCandidatesSupplier(PreferenceData<U, I> testData, Parser<U> uParser, Parser<I> iParser, String candidatesPath) {
        this.testData = testData;
        this.uParser = uParser;
        this.iParser = iParser;
        this.candidatesPath = candidatesPath;
    }

    @Override
    public Stream<IdObject<U, List<I>>> get() {
        BufferedReader candidatesReader;
        try {
            candidatesReader = new BufferedReader(new FileReader(candidatesPath));
        } catch (FileNotFoundException ex) {
            throw new UncheckedIOException(ex);
        }

        return candidatesReader.lines().parallel().map(line -> {
            CharSequence[] tokens = split(line, '\t', 3);
            final U user = uParser.parse(tokens[0]);
            final List<I> candidates = new ArrayList<>();
            for (CharSequence candidate : split(tokens[1], ',')) {
                candidates.add(iParser.parse(candidate));
            }
            testData.getUserPreferences(user).forEach(iv -> candidates.add(iv.id));
            
            return new IdObject<>(user, candidates);
        });
    }
}
