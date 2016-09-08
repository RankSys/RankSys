/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs;

/**
 * Empty CODEC that does not do anything but copying the array.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class NullCODEC extends AbstractCODEC<int[]> {

    @Override
    public int[] co(int[] in, int offset, int len) {
        add(len * Integer.BYTES, len * Integer.BYTES);
        int[] out = new int[len];
        System.arraycopy(in, offset, out, 0, len);
        return out;
    }

    @Override
    public int dec(int[] in, int[] out, int outOffset, int len) {
        System.arraycopy(in, 0, out, outOffset, len);
        return len;
    }

    @Override
    public boolean isIntegrated() {
        return true; // JUST TO AVOID D-GAPS, USELESS HERE
    }

}
