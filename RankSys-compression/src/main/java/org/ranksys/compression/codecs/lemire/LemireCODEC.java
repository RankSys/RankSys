/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs.lemire;

import java.util.Arrays;
import java.util.function.Supplier;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.differential.IntegratedIntegerCODEC;
import org.ranksys.compression.codecs.AbstractCODEC;

/**
 * Wrapper for Lemire's JavaFastPFOR integer compression library.
 * 
 * Check https://github.com/lemire/JavaFastPFOR
 *
 * @author Saúl Vargas (saul.vargas@glasgow.ac.uk)
 */
public class LemireCODEC extends AbstractCODEC<int[]> {

    private final Supplier<IntegerCODEC> supplier;
    private final boolean integrated;

    /**
     * Constructor.
     *
     * @param supplier supplier of an IntegerCODEC used internally
     */
    public LemireCODEC(Supplier<IntegerCODEC> supplier) {
        this.supplier = supplier;
        this.integrated = supplier.get() instanceof IntegratedIntegerCODEC;
    }

    @Override
    public int[] co(int[] in, int offset, int len) {
        IntegerCODEC codec = supplier.get();
        int[] out = new int[len + 1024];
        IntWrapper inputoffset = new IntWrapper(offset);
        IntWrapper outputoffset = new IntWrapper(1);
        codec.compress(in, inputoffset, len, out, outputoffset);
        out[0] = outputoffset.get() - 1;
        out = Arrays.copyOf(out, outputoffset.get());
        add(len * Integer.BYTES, outputoffset.intValue() * Integer.BYTES);

        return out;
    }

    @Override
    public int dec(int[] in, int[] out, int outOffset, int len) {
        IntegerCODEC codec = supplier.get();
        int nInts = in[0];
        IntWrapper inputoffset = new IntWrapper(1);
        IntWrapper outputoffset = new IntWrapper(outOffset);
        codec.uncompress(in, inputoffset, nInts, out, outputoffset);

        return inputoffset.get();
    }

    @Override
    public boolean isIntegrated() {
        return integrated;
    }

}
