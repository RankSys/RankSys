/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs.lemire;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.lemire.integercompression.Composition;
import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.VariableByte;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.ranksys.compression.codecs.AbstractCODEC;

/**
 * FastPFOR coding (with Variable Byte as fallback).
 *
 * @author Saúl Vargas (saul.vargas@glasgow.ac.uk)
 */
public class FastPFORVBCODEC extends AbstractCODEC<int[]> {

    private static final Logger LOG = Logger.getLogger(FastPFORVBCODEC.class.getName());

    private transient volatile GenericObjectPool<IntegerCODEC> pool;
    private final boolean integrated;

    /**
     * Constructor.
     */
    public FastPFORVBCODEC() {
        this.pool = null;
        this.integrated = false;
    }

    @Override
    public int[] co(int[] in, int offset, int len) {
        IntegerCODEC pfor;
        try {
            if (pool == null) {
                synchronized (this) {
                    if (pool == null) {
                        pool = new GenericObjectPool<>(new FastPFORFactory());
                    }
                }
            }
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
            if (pool == null) {
                synchronized (this) {
                    if (pool == null) {
                        pool = new GenericObjectPool<>(new FastPFORFactory());
                    }
                }
            }
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

    private static class FastPFORFactory extends BasePooledObjectFactory<IntegerCODEC> implements Serializable {

        @Override
        public IntegerCODEC create() throws Exception {
            return new Composition(new FastPFOR(), new VariableByte());
        }

        @Override
        public PooledObject<IntegerCODEC> wrap(IntegerCODEC pfor) {
            return new DefaultPooledObject<>(pfor);
        }
    }
}
