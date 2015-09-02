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
public interface CODEC<T> {

    public T co(int[] in, int offset, int len);

    public int dec(T t, int[] out, int outOffset, int len);

    public long[] stats();
    
    public void reset();
    
    public boolean isIntegrated();
}
