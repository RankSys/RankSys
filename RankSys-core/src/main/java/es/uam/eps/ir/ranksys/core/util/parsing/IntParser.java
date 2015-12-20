/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

import static java.util.stream.IntStream.range;

/**
 * Parses a CharSequence to an int
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public interface IntParser {

    /**
     * Parses a CharSequence into an int.
     *
     * @param from string to be parsed
     * @return parsed int
     */
    public int parse(CharSequence from);

    /**
     * Default int parser
     */
    public static final IntParser dip = from -> {
        boolean neg = from.charAt(0) == '-';
        int x = range(neg ? 1 : 0, from.length()).map(i -> from.charAt(i) - '0').reduce(0, (a, b) -> a * 10 + b);
        return neg ? -x : x;
    };
}
