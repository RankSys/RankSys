/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs.catena;

import org.ranksys.compression.codecs.AbstractCODEC;
import eu.nicecode.groupvarint.GroupVarint;
import java.util.Arrays;

/**
 * Matteo Catena's implementation of Group Varint.
 * 
 * https://github.com/catenamatteo/groupvarint
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class GroupVByteCODEC extends AbstractCODEC<byte[]> {

    private final GroupVarint groupVarint;

    /**
     * Constructor.
     */
    public GroupVByteCODEC() {
        this.groupVarint = new GroupVarint();
    }

    @Override
    public byte[] co(int[] in, int offset, int len) {
        byte[] out = new byte[GroupVarint.getSafeCompressedLength(in.length)];

        int writtenBytes = groupVarint.compress(in, offset, len, out, 0);
        out = Arrays.copyOf(out, writtenBytes);

        add(len * Integer.BYTES, out.length * Byte.BYTES);

        return out;
    }

    @Override
    public int dec(byte[] t, int[] out, int outOffset, int len) {
        groupVarint.uncompress(t, 0, out, outOffset, len);

        return 0;
    }

    @Override
    public boolean isIntegrated() {
        return false;
    }

}
