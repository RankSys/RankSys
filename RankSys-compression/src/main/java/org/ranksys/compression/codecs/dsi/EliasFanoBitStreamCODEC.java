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
 * Elias-Fano coding.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class EliasFanoBitStreamCODEC extends BitStreamCODEC {

    @Override
    protected void write(OutputBitStream obs, int[] in, int offset, int len) throws IOException {
        int l = 31 - Integer.numberOfLeadingZeros(in[offset + len - 1] / len);
        int mask = (1 << l) - 1;

        if (l < 1) {
            l = 1;
            mask = 1;
        }

        int d = 0;
        obs.writeInt(l, 32);
        for (int i = offset; i < offset + len; i++) {
            final int x = in[i];
            final int hx = x >> l;
            obs.writeUnary(hx - d);
            obs.writeInt(x & mask, l);
            d = hx;
        }
    }

    @Override
    protected void read(InputBitStream ibs, int[] out, int offset, int len) throws IOException {
        int d = 0;
        final int l = ibs.readInt(32);
        for (int i = offset; i < offset + len; i++) {
            final int hx = (d += ibs.readUnary());
            out[i] = hx << l | ibs.readInt(l);
        }
    }

    @Override
    public boolean isIntegrated() {
        return true;
    }

}
