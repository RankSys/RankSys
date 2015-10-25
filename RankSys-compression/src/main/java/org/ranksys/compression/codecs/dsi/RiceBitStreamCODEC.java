/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs.dsi;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import java.io.IOException;

/**
 * Rice coding.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class RiceBitStreamCODEC extends BitStreamCODEC {

    @Override
    protected void write(OutputBitStream obs, int[] in, int offset, int len) throws IOException {
        long sum = 0;
        for (int i = offset; i < offset + len; i++) {
            sum += in[i];
        }
        int b = (int) (0.69 * sum / (double) len);
        if (b == 0) {
            b = 1;
        }
        int log2b = 31 - Integer.numberOfLeadingZeros(b);

        obs.writeInt(log2b, 32);
        for (int i = offset; i < offset + len; i++) {
            obs.writeUnary(in[i] >> log2b);
            obs.writeInt(in[i] & ((1 << log2b) - 1), log2b);
        }
    }

    @Override
    protected void read(InputBitStream ibs, int[] out, int offset, int len) throws IOException {
        final int log2b = ibs.readInt(32);
        for (int i = offset; i < offset + len; i++) {
            final int q = ibs.readUnary();
            out[i] = log2b == 0 ? q : (q << log2b) | ibs.readInt(log2b);
        }
    }

}
