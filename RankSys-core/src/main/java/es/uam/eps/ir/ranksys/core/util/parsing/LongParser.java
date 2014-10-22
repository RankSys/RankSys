/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma de Madrid, http://ir.ii.uam.es
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

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class LongParser implements Parser<Long> {

    @Override
    public Long parse(CharSequence seq) {
        long val = 0;

        for (int i = 0; i < seq.length(); i++) {
            val = (seq.charAt(i) - '0') + val * 10;
        }

        return val;
    }
}
