/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.util;

/**
 * Delta gaps support.
 * 
 * Sorted integer arrays are rewritten so that:
 * b[0] = a[0] + 1
 * b[n] = a[n] - a[n - 1] for i &ge; 0
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class Delta {

    /**
     * Converts sorted array into d-gaps.
     *
     * @param a array of sorted integers
     * @param offset offset from where to start converting
     * @param len number of integers to convert
     */
    public static void delta(int[] a, int offset, int len) {
        for (int i = len + offset - 1; i > offset; --i) {
            a[i] -= a[i - 1];
        }
        a[offset]++;
    }

    /**
     * Restores a d-gap array into the original integers.
     *
     * @param a array of d-gaps
     * @param offset offset from where to start converting
     * @param len number of integers to convert
     */
    public static void atled(int[] a, int offset, int len) {
        a[offset]--;
        for (int i = offset + 1; i < offset + len; ++i) {
            a[i] += a[i - 1];
        }
    }
    
}
