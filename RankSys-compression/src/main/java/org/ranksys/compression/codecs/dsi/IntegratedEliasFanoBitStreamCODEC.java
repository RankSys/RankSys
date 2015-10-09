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
package org.ranksys.compression.codecs.dsi;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import java.io.IOException;

/**
 * Elias-Fano coding.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class IntegratedEliasFanoBitStreamCODEC extends BitStreamCODEC {

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
