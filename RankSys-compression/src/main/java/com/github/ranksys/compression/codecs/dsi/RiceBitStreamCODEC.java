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
package com.github.ranksys.compression.codecs.dsi;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import java.io.IOException;

/**
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class RiceBitStreamCODEC extends BitStreamCODEC {

    @Override
    public void write(OutputBitStream obs, int[] in, int offset, int len) throws IOException {
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
    public void read(InputBitStream ibs, int[] out, int offset, int len) throws IOException {
        final int log2b = ibs.readInt(32);
        for (int i = offset; i < offset + len; i++) {
            final int q = ibs.readUnary();
            out[i] = log2b == 0 ? q : (q << log2b) | ibs.readInt(log2b);
        }
    }

}
