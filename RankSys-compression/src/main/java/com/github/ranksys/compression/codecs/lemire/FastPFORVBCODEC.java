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
import java.util.logging.Level;
import java.util.logging.Logger;
import me.lemire.integercompression.Composition;
import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.VariableByte;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.github.ranksys.compression.codecs.AbstractCODEC;

/**
 *
 * @author Saúl Vargas (saul.vargas@glasgow.ac.uk)
 */
public class FastPFORVBCODEC extends AbstractCODEC<int[]> {

    private static final Logger LOG = Logger.getLogger(FastPFORVBCODEC.class.getName());

    private final IntegerCODECPool pool;
    private final boolean integrated;

    public FastPFORVBCODEC() {
        this.pool = new IntegerCODECPool(() -> new Composition(new FastPFOR(), new VariableByte()));
        this.integrated = false;
    }

    @Override
    public int[] co(int[] in, int offset, int len) {
        IntegerCODEC pfor;
        try {
            pfor = pool.borrowObject();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
        int[] out = new int[len + 1024];
        IntWrapper inputoffset = new IntWrapper(offset);
        IntWrapper outputoffset = new IntWrapper(1);
        pfor.compress(in, inputoffset, len, out, outputoffset);
        out[0] = outputoffset.get() - 1;
        out = Arrays.copyOf(out, outputoffset.get());
        pool.returnObject(pfor);
        add(len * Integer.BYTES, outputoffset.intValue() * Integer.BYTES);

        return out;
    }

    @Override
    public int dec(int[] in, int[] out, int outOffset, int len) {
        IntegerCODEC pfor;
        try {
            pfor = pool.borrowObject();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return -1;
        }
        int nInts = in[0];
        IntWrapper inputoffset = new IntWrapper(1);
        IntWrapper outputoffset = new IntWrapper(outOffset);
        pfor.uncompress(in, inputoffset, nInts, out, outputoffset);
        pool.returnObject(pfor);

        return inputoffset.get();
    }

    @Override
    public boolean isIntegrated() {
        return integrated;
    }

    public static class IntegerCODECPool extends GenericObjectPool<IntegerCODEC> {

        public IntegerCODECPool(Supplier<IntegerCODEC> supplier) {
            super(new IntegerCODECFactory(supplier));
        }

        public IntegerCODECPool(Supplier<IntegerCODEC> supplier, GenericObjectPoolConfig config) {
            super(new IntegerCODECFactory(supplier), config);
        }

        public IntegerCODECPool(Supplier<IntegerCODEC> supplier, GenericObjectPoolConfig config, AbandonedConfig abandonedConfig) {
            super(new IntegerCODECFactory(supplier), config, abandonedConfig);
        }

    }

    private static class IntegerCODECFactory extends BasePooledObjectFactory<IntegerCODEC> {

        private final Supplier<IntegerCODEC> supplier;

        public IntegerCODECFactory(Supplier<IntegerCODEC> supplier) {
            this.supplier = supplier;
        }

        @Override
        public IntegerCODEC create() throws Exception {
            return supplier.get();
        }

        @Override
        public PooledObject<IntegerCODEC> wrap(IntegerCODEC pfor) {
            return new DefaultPooledObject<>(pfor);
        }
    }
}
