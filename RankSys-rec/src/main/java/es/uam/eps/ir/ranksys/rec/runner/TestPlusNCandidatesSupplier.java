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

    private final PreferenceData<U, I, ?> testData;
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
    public TestPlusNCandidatesSupplier(PreferenceData<U, I, ?> testData, Parser<U> uParser, Parser<I> iParser, String candidatesPath) {
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
