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
package org.ranksys.compression.codecs.lemire;

import me.lemire.integercompression.Composition;
import me.lemire.integercompression.OptPFD;
import me.lemire.integercompression.VariableByte;

/**
 * OptPFD coding (with Variable Byte as fallback).
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class OptPFDVBCODEC extends LemireCODEC{

    /**
     * Constructor.
     */
    public OptPFDVBCODEC() {
        super(() -> new Composition(new OptPFD(), new VariableByte()));
    }
    
}
