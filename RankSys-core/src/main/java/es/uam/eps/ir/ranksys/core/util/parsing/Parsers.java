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
package es.uam.eps.ir.ranksys.core.util.parsing;

import java.util.stream.IntStream;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class Parsers {

    public static Parser<Integer> ip = from -> IntStream.range(0, from.length()).map(i -> (from.charAt(i) - '0')).reduce(0, (a, b) -> a * 10 + b);
    public static Parser<Long> lp = from -> IntStream.range(0, from.length()).mapToLong(i -> (from.charAt(i) - '0')).reduce(0, (a, b) -> a * 10 + b);
    public static Parser<String> sp = from -> from.toString();
    public static Parser<Float> fp = from -> Float.parseFloat(from.toString());
    public static Parser<Double> dp = from -> Double.parseDouble(from.toString());
    public static Parser<Void> vp = from -> null;
}
