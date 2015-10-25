/* 
 * Copyright (C) 2015 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.core.util.parsing;

import java.util.stream.IntStream;

/**
 * Generic implementations of the interface Parser.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Parsers {

    /**
     * Parse to Integer.
     */
    public static Parser<Integer> ip = from -> {
        int n = from.charAt(0) == '-' ? 1 : 0;
        int m = from.charAt(0) == '-' ? -1 : 1;
        return m * IntStream.range(n, from.length()).map(i -> (from.charAt(i) - '0')).reduce(0, (a, b) -> a * 10 + b);
    };

    /**
     * Parse to Long.
     */
    public static Parser<Long> lp = from -> {
        int n = from.charAt(0) == '-' ? 1 : 0;
        int m = from.charAt(0) == '-' ? -1 : 1;
        return m * IntStream.range(n, from.length()).mapToLong(i -> (from.charAt(i) - '0')).reduce(0, (a, b) -> a * 10 + b);
    };

    /**
     * Parse to String.
     */
    public static Parser<String> sp = from -> from.toString();

    /**
     * Parse to Float.
     */
    public static Parser<Float> fp = from -> Float.parseFloat(from.toString());

    /**
     * Parse to Double.
     */
    public static Parser<Double> dp = from -> Double.parseDouble(from.toString());

    /**
     * Parse to Void.
     */
    public static Parser<Void> vp = from -> null;
}
