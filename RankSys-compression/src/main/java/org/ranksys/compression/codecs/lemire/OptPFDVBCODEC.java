/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs.lemire;

import java.io.Serializable;
import java.util.function.Supplier;
import me.lemire.integercompression.Composition;
import me.lemire.integercompression.IntegerCODEC;
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
        super((Supplier<IntegerCODEC> & Serializable) () -> new Composition(new OptPFD(), new VariableByte()));
    }
    
}
