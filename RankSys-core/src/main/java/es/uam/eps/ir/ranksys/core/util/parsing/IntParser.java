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
