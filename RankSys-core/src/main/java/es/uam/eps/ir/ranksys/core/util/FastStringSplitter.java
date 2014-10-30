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
package es.uam.eps.ir.ranksys.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FastStringSplitter {

    public static CharSequence[] split(String line, int delimiter) {
        List<Integer> l = limits(line, delimiter);

        CharSequence[] tokens = new CharSequence[l.size() - 1];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = new StringSegment(line, l.get(i) + 1, l.get(i + 1));
        }

        return tokens;
    }

    public static CharSequence[] split(CharSequence line, int delimiter) {
        List<Integer> l = limits(line, delimiter);

        CharSequence[] tokens = new CharSequence[l.size() - 1];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = new StringSegment(line, l.get(i) + 1, l.get(i + 1));
        }

        return tokens;
    }

    private static List<Integer> limits(String line, int delimiter) {
        List<Integer> l = new ArrayList<>();

        int j = -1;
        do {
            l.add(j);
            j = line.indexOf(delimiter, j + 1);
        } while (j != -1);
        l.add(line.length());

        return l;
    }

    private static List<Integer> limits(CharSequence line, int delimiter) {
        List<Integer> l = new ArrayList<>();

        int j = -1;
        do {
            l.add(j);
            j = indexOf(line, delimiter, j + 1);
        } while (j != -1);
        l.add(line.length());

        return l;
    }

    private static int indexOf(CharSequence line, int ch, int fromIndex) {
        final int max = line.length();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }

        for (int i = fromIndex; i < max; i++) {
            if (line.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    private static class StringSegment implements CharSequence {

        private final CharSequence charSeq;
        private final int s;
        private final int e;

        public StringSegment(CharSequence cs, int s, int e) {
            this.charSeq = cs;
            this.s = s;
            this.e = e;
        }

        @Override
        public int length() {
            return e - s;
        }

        @Override
        public char charAt(int index) {
            return charSeq.charAt(s + index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new StringSegment(charSeq, s + start, s + end);
        }

        @Override
        public String toString() {
            return charSeq.toString().substring(s, e);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof CharSequence)) {
                return false;
            }
            CharSequence cs = (CharSequence) obj;
            if (length() != cs.length()) {
                return false;
            }
            for (int i = 0; i < length(); i++) {
                if (charAt(i) != cs.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hc = 0;
            for (int i = 0; i < length(); i++) {
                hc = (hc * 31 + charAt(i));
            }
            return hc;
        }
    }
}
