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
 * Fixed-length coding.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class FixedLengthBitStreamCODEC extends BitStreamCODEC {

    private final int b;

    /**
     * Constructor.
     *
     * @param b fixed number of bytes to use for each integer
     */
    public FixedLengthBitStreamCODEC(int b) {
        this.b = b;
    }

    @Override
    protected void write(OutputBitStream obs, int[] in, int offset, int len) throws IOException {
        for (int i = offset; i < offset + len; i++) {
            obs.writeInt(in[i], b);
        }
    }

    @Override
    protected void read(InputBitStream ibs, int[] out, int offset, int len) throws IOException {
        for (int i = offset; i < offset + len; i++) {
            out[i] = ibs.readInt(b);
        }
    }

}
