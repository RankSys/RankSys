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
package es.uam.eps.ir.ranksys.core.util;

/**
 * Efficient and fast CharSequence splitter.
 * 
 * It is a much faster alternative to the String.split method.
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class FastStringSplitter {

    private static final int N = 100;
    
    /**
     * Splits a CharSequence according to the delimiter character.
     *
     * @param line CharSequence to be split.
     * @param delimiter delimiter character.
     * @return an array of CharSequence's resulting from the split
     */
    public static CharSequence[] split(CharSequence line, int delimiter) {
        CharSequence[] tokens0 = split(line, delimiter, N);
        
        if (tokens0.length < N) {
            return tokens0;
        } else {
            CharSequence[] tokens1 = split(tokens0[N - 1], delimiter);
            CharSequence[] tokens2 = new CharSequence[tokens0.length + tokens1.length - 1];
            System.arraycopy(tokens0, 0, tokens2, 0, tokens0.length - 1);
            System.arraycopy(tokens1, 0, tokens2, tokens0.length - 1, tokens1.length);
            
            return tokens2;
        }
    }
    
    /**
     * Splits a CharSequence according to the delimiter character in a limited number of tokens.
     *
     * @param line CharSequence to be split.
     * @param delimiter delimiter character.
     * @param n maximum number of tokens to split the input
     * @return an array of CharSequence's resulting from the split
     */
    public static CharSequence[] split(CharSequence line, int delimiter, int n) {
        int[] l = limits(line, delimiter, n);

        CharSequence[] tokens = new CharSequence[l.length - 1];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = new StringSegment(line, l[i] + 1, l[i + 1]);
        }

        return tokens;
    }

    private static int[] limits(CharSequence line, int delimiter, int n) {
        int[] l0 = new int[n + 1];

        int i = 0;
        int j = -1;
        do {
            l0[i] = j;
            i++;
            j = indexOf(line, delimiter, j + 1);
        } while (j != -1 && i <= n - 1);
        l0[i] = line.length();

        int[] l = new int[i + 1];
        System.arraycopy(l0, 0, l, 0, l.length);

        return l;
    }

    private static int indexOf(CharSequence line, int ch, int fromIndex) {
        if (line instanceof String) {
            return ((String) line).indexOf(ch, fromIndex);
        }
        
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
