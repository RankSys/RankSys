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
 * Zeta coding.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class ZetaBitStreamCODEC extends BitStreamCODEC {

    private final int k;

    /**
     * Constructor.
     *
     * @param k parameter k of the coding
     */
    public ZetaBitStreamCODEC(int k) {
        this.k = k;
    }

    @Override
    protected void write(OutputBitStream obs, int[] in, int offset, int len) throws IOException {
        for (int i = offset; i < offset + len; i++) {
            obs.writeZeta(in[i], k);
        }
    }

    @Override
    protected void read(InputBitStream ibs, int[] out, int offset, int len) throws IOException {
        ibs.readZetas(k, out, len);
    }

}
