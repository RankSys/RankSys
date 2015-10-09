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
