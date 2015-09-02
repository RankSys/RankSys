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
package com.github.ranksys.compression.codecs;

/**
 *
 * @author Saúl Vargas (saul.vargas@glasgow.ac.uk)
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
