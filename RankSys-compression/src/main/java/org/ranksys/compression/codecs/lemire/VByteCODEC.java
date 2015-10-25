/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.compression.codecs.lemire;

import me.lemire.integercompression.VariableByte;

/**
 * Variable byte coding.
 *
 * @author Saúl Vargas (Saul.Vargas@glasgow.ac.uk)
 */
public class VByteCODEC extends LemireCODEC {

    /**
     * Constructor.
     */
    public VByteCODEC() {
        super(() -> new VariableByte());
    }

}
