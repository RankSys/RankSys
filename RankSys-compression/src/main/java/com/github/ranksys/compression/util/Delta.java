/* 
 * Copyright (C) 2015 RankSys http://ranksys.github.io
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
package com.github.ranksys.compression.util;

import java.util.function.IntUnaryOperator;

/**
 *
 * @author Saúl Vargas (saul.vargas@glasgow.ac.uk)
 */
public class Delta {

    public static void delta(int[] a, int offset, int len) {
        for (int i = len + offset - 1; i > offset; --i) {
            a[i] -= a[i - 1];
        }
        a[offset]++;
    }
    
    public static IntUnaryOperator delta() {
        int[] d = {-1};
        return i -> {
            int v = i - d[0];
            d[0] = i;
            return v;
        };
    }

    public static void atled(int[] a, int offset, int len) {
        a[offset]--;
        for (int i = offset + 1; i < offset + len; ++i) {
            a[i] += a[i - 1];
        }
    }
    
    public static IntUnaryOperator atled() {
        int[] d = {-1};
        return i -> d[0] += i;
    }

}
