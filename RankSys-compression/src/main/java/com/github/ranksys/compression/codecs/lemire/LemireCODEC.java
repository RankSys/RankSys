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
package com.github.ranksys.compression.codecs.lemire;

import java.util.Arrays;
import java.util.function.Supplier;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.differential.IntegratedIntegerCODEC;
import com.github.ranksys.compression.codecs.AbstractCODEC;

/**
 *
 * @author Saúl Vargas (saul.vargas@glasgow.ac.uk)
 */
public class LemireCODEC extends AbstractCODEC<int[]> {

    private final Supplier<IntegerCODEC> supplier;
    private final boolean integrated;

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
