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

import it.unimi.dsi.io.OutputBitStream;
import it.unimi.dsi.io.InputBitStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.github.ranksys.compression.codecs.AbstractCODEC;

/**
 * Wrapper of dsiutils' BitStreams.
 * 
 * Check http://dsiutils.di.unimi.it/
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public abstract class BitStreamCODEC extends AbstractCODEC<byte[]> {

    private static final Logger LOG = Logger.getLogger(BitStreamCODEC.class.getName());

    @Override
    public byte[] co(int[] in, int offset, int len) {

        byte[] out = new byte[len * 4 + 1024];
        int writtenBits = 0;
        try (OutputBitStream obs = new OutputBitStream(out)) {
            write(obs, in, offset, len);
            writtenBits = (int) obs.writtenBits();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        out = Arrays.copyOf(out, (writtenBits + 7) / 8);

        add(len * Integer.BYTES, out.length * Byte.BYTES);

        return out;
    }

    /**
     * Write integer array to BitStream.
     *
     * @param obs output bit stream
     * @param in input array
     * @param offset offset of array
     * @param len number of bytes to write
     * @throws IOException when IO error
     */
    protected abstract void write(OutputBitStream obs, int[] in, int offset, int len) throws IOException;

    @Override
    public int dec(final byte[] in, final int[] out, final int outOffset, final int len) {
        try (InputBitStream ibs = new InputBitStream(in)) {
            read(ibs, out, outOffset, len);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    /**
     * Reads a BitStream to an array.
     *
     * @param ibs input bit stream
     * @param out output array
     * @param offset array offset
     * @param len number of integers to read
     * @throws IOException when IO error
     */
    protected abstract void read(final InputBitStream ibs, final int[] out, final int offset, final int len) throws IOException;

    @Override
    public boolean isIntegrated() {
        return false;
    }

}
